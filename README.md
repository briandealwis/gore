GORE: Go Runtime for Eclipse
============================

Provides a simple Go [language server binding](https://langserver.org) to
use the [Bingo][bingo] language server within Eclipse.

## Building

```sh
$ sh update-go-langserver.sh
$ mvn package
```

## Updating Dependencies

GORE includes prebuilt copies of [Bingo][bingo].  To update them,
either use `gox` or manually compile (preferably with `-ldflags=-w`
to produce smaller binaries).

  [bingo]: github.com/saibing/bingo
