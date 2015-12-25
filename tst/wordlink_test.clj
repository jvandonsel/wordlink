(ns wordlink-test
    (:require [clojure.test :refer :all]
      [wordlink :refer :all]
      ))


(deftest remove-cycles-test
  (is (= '[a b c d g] (remove-cycles '[a b c d e f b c d g] )))
   )

(deftest distance-test
  (is (= 2 (distance "aaabca" "aaaaaa")))
  (is (= 0 (distance "aaa" "aaa")))
  )