(ns not-your-man.macros)

(defmacro read-file
  [file-path]
  (slurp file-path))
