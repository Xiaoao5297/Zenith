@echo off
echo go version:

go version

echo.
echo.
echo Download dependencies ...

go mod tidy

echo Compiling...

go build -o scaxe-server.exe ./cmd/server

echo Done.
pause