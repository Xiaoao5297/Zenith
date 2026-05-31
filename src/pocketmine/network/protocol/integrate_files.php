<?php
// 获取目录中所有非.bak文件的列表
$directory = '/data/data/com.termux/files/home/PocketMine/src/pocketmine/network/protocol';
$files = glob($directory . '/*.php');

// 过滤掉.bak文件
$files = array_filter($files, function($file) {
    return !preg_match('/\.bak$/', $file);
});

// 创建输出文件
$outputFile = $directory . '/integrated_files.txt';
$output = '';

// 读取每个文件并添加到输出
foreach ($files as $file) {
    $filename = basename($file);
    $content = file_get_contents($file);
    
    // 添加文件名和内容到输出
    $output .= "文件名: $filename\n";
    $output .= "内容:\n$content\n";
    $output .= "====================\n\n";
}

// 写入输出文件
file_put_contents($outputFile, $output);

echo "整合完成！输出文件: $outputFile\n";
?>