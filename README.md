GORE: Go Runtime for Eclipse
============================

GORE is a hack to add language support for [Go](https://golang.org)
within Eclipse.  It configures tbe [lsp4e](lsp4e) and [tm4e](tm4e)
projects to use the [Gopls][gopls]
[language server](https://langserver.org) with highlighting and
language-configuration support.

The TextMate grammars are copied from

    https://github.com/syscrusher/golang.tmbundle/

## Building

```sh
$ mvn package
```

and install from `site/target/repository`.

## Updating Dependencies

GORE requires that you have `gopls` installed on your system.
GORE looks in the following locations:

  - $GOBIN
  - $GOPATH/bin
  - `go env GOBIN`
  - `go env GOPATH`
  - $PATH

See the [`gopls` installation instructions](gopls-install) for how to
install `gopls`.  This often seems to do the trick:
```sh
(cd /tmp; GO111MODULE=on go get golang.org/x/tools/gopls\@latest)
```

  [lsp4e]: https://wiki.eclipse.org/LSP4E
  [tm4e]: https://github.com/eclipse/tm4e
  [gopls]: https://github.com/golang/tools/blob/master/gopls/doc/
  [gopls-install]: https://github.com/golang/tools/blob/master/gopls/doc/user.md#installation
