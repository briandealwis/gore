#!/bin/sh
set -o errexit
GOLANGSERVERDIR=${GOPATH:-$HOME/go/src}/github.com/saibing/bingo

set -x
(cd $GOLANGSERVERDIR && gox -ldflags=-w -osarch 'darwin/amd64 windows/amd64 linux/amd64')
mkdir -p \
    ca.mt.gore/binaries/macosx.x86_64 \
    ca.mt.gore/binaries/linux.x86_64 \
    ca.mt.gore/binaries/win32.x86_64
rm -f ca.mt.gore/binaries/*/bingo*
cp -p $GOLANGSERVERDIR/bingo_darwin_amd64 ca.mt.gore/binaries/macosx.x86_64/bingo
cp -p $GOLANGSERVERDIR/bingo_linux_amd64 ca.mt.gore/binaries/linux.x86_64/bingo
cp -p $GOLANGSERVERDIR/bingo_windows_amd64.exe ca.mt.gore/binaries/win32.x86_64/bingo.exe

