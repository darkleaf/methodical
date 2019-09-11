(ns methodical.impl.method-table.clojure
  (:require [pretty.core :refer [PrettyPrintable]]
            [methodical.interface :as i]))

(deftype ClojureMethodTable [m]
  PrettyPrintable
  (pretty [_]
    (if (seq m)
      (list 'clojure-method-table (count m) 'primary)
      '(clojure-method-table)))

  Object
  (equals [_ another]
    (and (instance? ClojureMethodTable another)
         (= m (.m ^ClojureMethodTable another))))

  i/MethodTable
  (primary-methods [_]
    m)

  (aux-methods [_]
    nil)

  (add-primary-method [this dispatch-val method]
    (let [new-m (assoc m dispatch-val method)]
      (if (= m new-m)
        this
        (ClojureMethodTable. new-m))))

  (remove-primary-method [this dispatch-val]
    (let [new-m (dissoc m dispatch-val)]
      (if (= m new-m)
        this
        (ClojureMethodTable. new-m))))

  (add-aux-method [_ _ _ _]
    (throw (UnsupportedOperationException. "Clojure-style multimethods do not support auxiliary methods.")))

  (remove-aux-method [_ _ _ _]
    (throw (UnsupportedOperationException. "Clojure-style multimethods do not support auxiliary methods."))))
