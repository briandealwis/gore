GORE: Go Runtime for Eclipse
============================

GORE is a hack to add language support for [Go](https://golang.org)
within Eclipse.  It configures the most-excellent [lsp4e](lsp4e)
and [tm4e](tm4e) projects to use the [Gopls][gopls] or [Bingo][bingo]
[language servers](https://langserver.org) with highlighting and
language-configuration support.

The TextMate grammars are copied from

    https://github.com/syscrusher/golang.tmbundle/

## Building

```sh
$ mvn package
```

and install from `site/target/repository`.

## Updating Dependencies

GORE requires that you have `gopls` or `bingo` installed on your system.
GORE looks in the following locations:

  - $GOBIN
  - $GOPATH/bin
  - `go env GOBIN`
  - `go env GOPATH`
  - $PATH

See the [`gopls` installation instructions](gopls-install) for how to
install `gopls`.

  [lsp4e]: https://wiki.eclipse.org/LSP4E
  [tm4e]: https://github.com/eclipse/tm4e
  [gopls]: https://github.com/golang/tools/blob/master/gopls/doc/
  [gopls-install]: https://github.com/golang/tools/blob/master/gopls/doc/user.md#installation
  [bingo]: http://github.com/saibing/bingo
