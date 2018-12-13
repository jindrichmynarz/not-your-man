# not-your-man

A [re-frame](https://github.com/Day8/re-frame) application designed to ... well, that part is up to you.

Built using the [re-frame Leiningen template](https://github.com/Day8/re-frame-template).

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
