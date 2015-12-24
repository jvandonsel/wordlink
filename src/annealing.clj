;; Simple and naive simulated annealing example to find a chain of words
;; linking a start and end word, each differing by one letter.
;;
;; This isn't a very good application of simulated annealing since
;; here our goodness metric is perfect, i.e. we know when we've reached
;; the end word
;;
;; Jim Van Donsel, December 2015

(ns annealing
  (:require [clojure.string :as str])
  (:import (java.util Vector)))

;; Load dictionary
(def dict-file "/usr/share/dict/words")

;; Break dictionary into a set of words
(def word-set (set (str/split (str (slurp dict-file)) #"\n")))

;; The lowercase alphabet
(def alphabet (map char (range (int \a) (int \z))))

;; computes the distance between two words in terms of
;; number of letters that differ
(defn distance [a b]
  (reduce + (map #(if % 0 1) (map = a b))))

;; chooses a random (lower case) letter
(defn random-letter []
  (rand-nth alphabet))

;; chooses a random letter index in a word
(defn random-index [word]
  (int (rand (count word))))

;; Changes one letter of the current vector
(defn perturb [v]
  (str/join (assoc (vec v) (random-index v) (random-letter))))

;; Changes one letter of the current vector, insuring
;; that the result is a legal word.
(defn perturb-to-word [original]
  (loop [word original]
    (let [w (perturb word)]
      (cond
        (and (contains? word-set w) (not= w word)) w
        :else (recur word)))
    ))

; Given a temperature, returns random boolean representing
; whether to accept a less optimal value than the last one.
; Assumes temperature is on a 0-100 scale.
(defn temp-to-accept [temp]
  (let [
        thresh (/ temp 100)
        ]
    (< (rand 1) thresh)
    )
  )

;; Cooling
(defn cool [old-temp]
  (* old-temp 0.999)
  )

; Removes extraneous cycles in a vector
; for example [a b c d e f b c d g] --> [a b c d g]
(defn remove-cycles [v]
  (loop [
         unseen v
         result []
         ]

    (let [a (first unseen)
          r (rest unseen)
          next (.indexOf r a)]

      (cond
        (empty? unseen)
            result

        (neg? next)
            ; 'a' doesn't occur again in the future, just take it
            (recur r (conj result a))

        :else
            ; a occurs in the future. Drop everything up to the duplicate
            (recur (drop (inc next) r) (conj result a))
        )
      )
    ))

;; Do the actual simulated annealing loop, until we hit zero temperature,
;; or we hit the target.
;; Returns the word path as a vector, or nil if a path was not found.
(defn anneal [current-word target temperature path]
  (let [new-word          (perturb-to-word current-word)
        current-distance  (distance current-word target)
        new-distance      (distance new-word target)
        accept            (temp-to-accept temperature)
        new-temperature   (cool temperature)]
    ;(println "current:" current-word " new:" new-word " current-dist:" current-distance " T:" temperature "accept:" accept)
    (cond
      ;; Found our target
      (= target current-word) path

      ;; Reached zero temperature without hitting our target
      (< temperature 0.001) nil

      ;; Take the new word if it's better,
      ;; or if not better, sometimes take the new word anyway if accept is true
      (or
        (<= new-distance current-distance)
        accept
        ) (recur new-word target new-temperature (conj path new-word))

      ; Keep the old value
      :else (recur current-word target new-temperature path))))


;; Attempts to find a word path between two words
;; using simulated annealing.
;; The search stops when the temperature reaches zero, or if
;; we've found the end word.
;; Returns a vector of words from start to end, or []
;; if we've reached zero temperature without finding the target.
(defn find-path [start-word end-word]
  (let [initial-temperature 100
        path (anneal start-word end-word initial-temperature [start-word])
        ]
    (remove-cycles path)))

; Average the values in a vector
(defn avg [v]
  (float (/ (reduce + v) (count v))))

;; Run many trials of finding the path between 2 words, collecting their path lengths
(defn run-trials []
  (let [num-trials      1000
        start-word      "apple"
        end-word        "cider"
        paths           (repeatedly num-trials
                          #(find-path start-word end-word))
        non-empty-paths (filter not-empty paths)
        empty-paths     (filter empty? paths)
        lengths         (map count non-empty-paths)
        average-length  (avg lengths)
        minimum-length  (apply min lengths)
        maximum-length  (apply max lengths)
        length-map      (zipmap lengths non-empty-paths)
        shortest-path   (get length-map minimum-length)
        ]
    (println "non-empty-paths:" (count non-empty-paths) "empty-paths:" (count empty-paths))
    (println "avg=" average-length " min=" minimum-length " max=" maximum-length)
    (println "shortest path:" minimum-length shortest-path)
    ))






