(ns methodical.impl.combo.common-test
  (:require [clojure.test :refer :all]
            [methodical.impl.combo.common :as combo.common]))

(deftest combine-primary-methods-test
  (testing "Primary methods should get an implicit `next-method` arg when combined"
    (let [f (combo.common/combine-primary-methods
             [(fn [next-method v]
                (cond-> (conj v :primary-1) next-method next-method))
              (fn [next-method v]
                (cond-> (conj v :primary-2) next-method next-method))])]
      (is (= [:primary-1 :primary-2]
             (f [])))))

  (testing "Should be able to call `next-method` before at any point in the method body"
    (let [f (combo.common/combine-primary-methods
             [(fn [next-method v]
                (conj (cond-> v next-method next-method) :primary-1))
              (fn [next-method v]
                (conj (cond-> v next-method next-method) :primary-2))])]
      (is (= [:primary-2 :primary-1]
             (f [])))))

  (testing "`combine-primary-methods` should return `nil` if `primary-methods` is empty"
    (is (= nil
           (combo.common/combine-primary-methods
            [])))))

(deftest apply-around-methods-test
  (testing "Apply-around-methods with one arg"
    (let [f (combo.common/apply-around-methods
             (fn [v] (conj v :primary))
             [(fn [next-method v]
                (-> (conj v :around-1-before)
                    next-method
                    (conj :around-1-after)))
              (fn [next-method v]
                (-> (conj v :around-2-before)
                    next-method
                    (conj :around-2-after)))])]
      (is (= [:around-2-before :around-1-before :primary :around-1-after :around-2-after]
             (f []))
          "Around methods should be called with implicit `next-method` arg, most-specific methods first.")))

  (testing "apply-around-methods with multiple args"
    (let [f (combo.common/apply-around-methods
             (fn [acc a b c] (conj acc [:primary a b c]))
             [(fn [next-method acc a b c]
                (-> acc
                    (conj [:around-1-before a b c])
                    (next-method a b c)
                    (conj [:around-1-after a b c])))
              (fn [next-method acc a b c]
                (-> acc
                    (conj [:around-2-before a b c])
                    (next-method a b c)
                    (conj [:around-2-after a b c])))])]
      (is (= [[:around-2-before :a :b :c]
              [:around-1-before :a :b :c]
              [:primary :a :b :c]
              [:around-1-after :a :b :c]
              [:around-2-after :a :b :c]]
             (f [] :a :b :c))
          "Around methods should thread their values thru to to the combined before-primary-after method."))))
