;; Finds a chain of words linking a start and end word, each differing by one letter.
;;
;; Depending on the setting of 'use-annealing' we will either use simulated annealing
;; or fixed transition probabilities.
;;
;; Jim Van Donsel, December 2015

(ns wordlink
  (:require [clojure.string :as str]))

;; Set to true to use simulated annealing, otherwise
;; fixed probabilities will be used.
(def use-annealing false)

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
;; that the result is a legal word. The original word
;; will not be chosen.
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
        ;; Use either a probability based on temperature, or a fixed probability
        thresh (if use-annealing
                 (/ temp 100) ; temperature-based probability (annealing)
                 0.1) ; fixed probability
        ]
    (< (rand 1) thresh)
    )
  )

;; Cooling
(defn cool [old-temp]
  (* old-temp 0.999)
  )

; Average the values in a vector
(defn avg [v]
  (if (empty? v) 0
                 (float (/ (reduce + v) (count v))))
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
          ; find any future duplicate occurrence of 'a'
          next (.indexOf r a)]

      (cond
        (empty? unseen)
            result

        (neg? next)
            ; 'a' doesn't occur again in the future, just take it.
            (recur r (conj result a))

        :else
            ; 'a' occurs in the future. Drop everything up to its duplicate.
            (recur (drop (inc next) r) (conj result a))
        )
      )
    ))

;; Do the actual simulated annealing loop, until we hit zero temperature,
;; or we hit the target.
;;
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
      ;; or if not better, sometimes take the new word anyway if 'accept' is true
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
;;
;; Returns a vector of words from start to end, or []
;; if we've reached zero temperature without finding the target.
(defn find-path- [start-word end-word]
  (let [initial-temperature 100
          path (anneal start-word end-word initial-temperature [start-word])
          ]
      (remove-cycles path)))


;; Run multiple trials of find-path- to find a word chain between 2 words,
;; collecting their path lengths and printing statistics.
(defn find-path [start-word end-word num-trials]


  (cond
    ; Validate the start and end words
    (not (contains? word-set start-word))
        (throw (IllegalArgumentException. "Start word doesn't appear in our dictionary"))
    (not (contains? word-set end-word))
        (throw (IllegalArgumentException. "End word doesn't appear in our dictionary"))
    (not= (count start-word) (count end-word))
        (throw (IllegalArgumentException. "Start word and end word are not the same length"))

    :else

    ;; Valid start and and words
    (let [
          paths          (repeatedly num-trials
                                     #(find-path- start-word end-word))
          good-paths     (filter not-empty paths)
          bad-paths      (filter empty? paths)
          lengths        (map count good-paths)
          _              (if (empty? lengths) (throw (Exception. "No paths found.")) nil)
          average-length (avg lengths)
          minimum-length (apply min lengths)
          maximum-length (apply max lengths)
          length-map     (zipmap lengths good-paths)
          shortest-path  (get length-map minimum-length)
          ]
      (println "non-empty-paths:" (count good-paths) "empty-paths:" (count bad-paths))
      (println "avg-length:" average-length " min-length:" minimum-length " max-length:" maximum-length)
      (println "shortest path:" minimum-length shortest-path)
      )
    ))


(defn -main []
  (find-path "apple" "cider" 100))





