(ns not-your-man.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [not-your-man.core :as core])
  (:require-macros [not-your-man.macros :refer [read-file]]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
