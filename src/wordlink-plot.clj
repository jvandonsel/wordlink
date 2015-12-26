;; Plotting functions for visualizing the distance metric across multiple wordlink runs.

(ns wordlink-plot
  (:use
   incanter.core
   incanter.charts
   incanter.stats
   incanter.io
   wordlink
   ) )

(def start-word "apple")
(def end-word   "cider")

; Given a list of words, returns the distance for each word
(defn lengths [path]
 (map (fn [p] (distance p end-word)) path)
  )

;; Runs finds a path between start-word and end-word and
;; returns a vector of the distance for each word.
(defn get-path-distances []
  (lengths  (:shortest-path (find-path start-word end-word 1))) )

;; Plots a vector
(defn plot-y [ys]
  (xy-plot (vec (range (count ys))) ys))

;; Adds another set of points to a plot
(defn overlay [plot ys]
  (add-lines plot (vec (range (count ys))) ys))

;; Run n iterations of find-path, plotting each
(defn run-and-plot [n]
  (let [plot (xy-plot)]
    (view plot)
    (repeatedly n #(overlay plot (get-path-distances)))
    ))

;; find paths and plot their distances
(run-and-plot 10)

