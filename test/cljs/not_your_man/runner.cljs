(ns not-your-man.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [not-your-man.core-test]))

(doo-tests 'not-your-man.core-test)
