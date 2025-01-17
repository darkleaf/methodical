(ns methodical.impl.combo.clojure-test
  (:require [clojure.test :refer :all]
            [methodical.impl.combo.clojure :as combo.clojure]
            [methodical.interface :as i]))

(deftest aux-methods-test
  (is (thrown-with-msg?
       UnsupportedOperationException
       #"Clojure-style multimethods do not support auxiliary methods."
       (i/combine-methods
        (combo.clojure/->ClojureMethodCombination)
        [(constantly :a)] {:before [(constantly :b)]}))
      "Clojure method combinations should thrown an Exception if you try to combine aux methods with them."))
