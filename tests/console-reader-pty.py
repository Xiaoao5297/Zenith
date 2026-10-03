"""Run with Python 3 and a PHP 7 CLI with pthreads, pcntl and posix."""

import os
import pathlib
import pty
import select
import signal
import subprocess
import termios
import time
import unittest

ROOT = str(pathlib.Path(__file__).resolve().parents[1])
# Stub only the server infrastructure; exercise the real reader and editor.
PHP = r'''
namespace pocketmine {
    abstract class Thread extends \Thread {}
}
namespace pocketmine\utils {
    class Utils {
        public static function getOS(){ return "linux"; }
    }
    class MainLogger extends \Threaded {
        public $callback;
        public function setConsoleCallback($callback){ $this->callback = $callback; }
        public function setConsoleEditorActive($active){}
        public function warning($message){ echo "WARNING:", $message, "\n"; }
        public function error($message){ echo "ERROR:", $message, "\n"; }
    }
}
namespace {
    require "src/pocketmine/utils/ConsoleLineEditor.php";
    require "src/pocketmine/command/CommandReader.php";
    $logger = new \pocketmine\utils\MainLogger;
    $reader = new \pocketmine\command\CommandReader($logger);
    $stop = false;
    pcntl_signal(SIGINT, function() use (&$stop){ $stop = true; echo "INTERRUPTED\n"; });
    $end = microtime(true) + 5;
    while(!$stop and microtime(true) < $end){
        pcntl_signal_dispatch();
        while(($line = $reader->getLine()) !== null){
            echo "RECEIVED:", json_encode($line), "\n";
            if($line === "quit"){ $stop = true; }
        }
        usleep(10000);
    }
    $reader->shutdown();
    $reader->join();
    echo "DONE\n";
}
'''


class ConsoleInputTest(unittest.TestCase):
    def test_tty(self):
        for mode in ('normal', 'ignore-cr', 'no-isig', 'remapped-intr'):
            with self.subTest(mode=mode):
                master, slave = pty.openpty()
                attrs = termios.tcgetattr(slave)
                if mode in ('ignore-cr', 'remapped-intr'):
                    attrs[0] |= termios.IGNCR
                if mode == 'no-isig':
                    attrs[3] &= ~termios.ISIG
                if mode == 'remapped-intr':
                    attrs[6][termios.VINTR] = b'\x00'
                termios.tcsetattr(slave, termios.TCSANOW, attrs)
                original = termios.tcgetattr(slave)
                pid = os.fork()
                if pid == 0:
                    os.close(master)
                    os.login_tty(slave)
                    os.chdir(ROOT)
                    os.execvp('php', ['php', '-r', PHP])
                output = b''

                reaped = False

                def wait_for(marker):
                    nonlocal output
                    deadline = time.monotonic() + 3
                    while marker not in output and time.monotonic() < deadline:
                        if select.select([master], [], [], 0.05)[0]:
                            try:
                                output += os.read(master, 65536)
                            except OSError:
                                break
                    self.assertIn(marker, output, output.decode(errors='replace'))

                try:
                    wait_for(b'Genisys> ')
                    os.write(master, b'help\r')
                    wait_for(b'RECEIVED:"help"')
                    os.write(master, b'status\x1b[\r')
                    wait_for(b'RECEIVED:"status"')
                    os.write(master, b'\x03')
                    wait_for(b'INTERRUPTED')
                    wait_for(b'DONE')
                    self.assertEqual(original, termios.tcgetattr(slave))
                    self.assertNotIn(b'ERROR:', output)
                    deadline = time.monotonic() + 3
                    while time.monotonic() < deadline:
                        exited, status = os.waitpid(pid, os.WNOHANG)
                        if exited:
                            reaped = True
                            self.assertTrue(os.WIFEXITED(status), status)
                            self.assertEqual(0, os.WEXITSTATUS(status))
                            break
                        time.sleep(0.01)
                    self.assertTrue(reaped, 'PHP did not exit after DONE')
                finally:
                    if not reaped:
                        os.kill(pid, signal.SIGKILL)
                        os.waitpid(pid, 0)
                    os.close(master)
                    os.close(slave)

    def test_pipe_eof(self):
        result = subprocess.run(['php', '-r', PHP], cwd=ROOT,
                                input=b'help\nstatus\n', capture_output=True, timeout=8)
        self.assertEqual(0, result.returncode, (result.stdout, result.stderr))
        self.assertIn(b'RECEIVED:"help"', result.stdout)
        self.assertIn(b'RECEIVED:"status"', result.stdout)
        self.assertIn(b'Console input closed (EOF)', result.stdout)
        self.assertIn(b'DONE', result.stdout)


if __name__ == '__main__':
    unittest.main()
