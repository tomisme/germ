(ns gateworld.dev.wfc
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


; defn FindLowestEntropy(coefficient_matrix):
;   Return the cell that has the lowest greater-than-zero
;    entropy, defined as:
;     A cell with one valid pattern has 0 entropy
;     A cell with no valid patterns is a contradiction
;     Else: the entropy is based on the sum of the frequency
;      that the patterns appear in the source data, plus
;      use some random noise to break ties and near-ties.


; defn Observe(coefficient_matrix):
;  FindLowestEntropy()
;  If there is a contradiction, throw an error and quit
;  If all cells are at entropy 0, processing is complete:
;    Return CollapsedObservations()
;  Else:
;    Choose a pattern by a random sample, weighted by the
;     pattern frequency in the source data
;    Set the boolean array in this cell to false, except
;     for the chosen pattern


; defn Propagate(coefficient_matrix):
;   Loop until no more cells are left to be update:
;     For each neighboring cell:
;       For each pattern that is still potentially valid:
;         Compare this location in the pattern with the cell's values
;           If this point in the pattern no longer matches:
;             Set the array in the wave to false for this pattern
;             Flag this cell as needing to be updated in the next iteration


; defn Run():
;   PatternsFromSample()
;   BuildPropagator()
;   Loop until finished:
;     Observe()
;     Propagate()
;   OutputObservations()


(defn render-pattern
  [grid]
  (into [:div]
        (for [line grid]
          (into [:div {:style {:display "flex"}}]
                (for [row line]
                  [:div {:style {:width 25
                                 :height 25
                                 :border "1px solid grey"
                                 :margin 1
                                 :background-color row}}])))))


(defn render-patterns
  [patterns]
  (into [:div {:style {:display "flex"
                       :flex-wrap "wrap"}}]
        (map-indexed
         (fn [idx p]
          [:div {:style {:margin 5}}
           idx
           (render-pattern p)])
         patterns)))


(defn render-overlaps
  [pattern overlap-index]
  [:div {:style {:margin 60
                 :position "relative"}}
   (render-pattern pattern)
   [:div {:style {:position "absolute"
                  :left -30
                  :top -30
                  :opacity 0.3}}
    (render-pattern pattern)]])


;;


(def random
  (rand-int 9))


(def pattern-width 2)
(def pattern-height 2)


(defn build-offsets
  [pattern-width pattern-height]
  (let [min-x (- (dec pattern-width))
        min-y (- (dec pattern-height))
        max-x (dec pattern-width)
        max-y (dec pattern-height)]
   (loop [x min-x
          y min-y
          offsets []]
     (if (> x max-x)
       (recur min-x (inc y) offsets)
       (if (> y max-y)
         offsets
         (recur (inc x) y (conj offsets [x y])))))))


(defn get-pattern-at
  [sample x y]
  (vec (for [i (range pattern-height)]
          (subvec
           (nth sample (+ y i))
           x
           (+ x pattern-width)))))


(defn patterns-from-sample
  [sample]
  (let [sample-width (count (first sample))
        sample-height (count sample)]
    (loop [x 0
           y 0
           patterns []]
      (if (> x (- sample-width pattern-width))
        (recur 0 (inc y) patterns)
        (if (> y (- sample-height pattern-height))
          patterns
          (recur (inc x) y (conj patterns (get-pattern-at sample x y))))))))


;; does p2 (with x/y offset) fit on top of p1?
(defn overlaps?
  [p1 p2 x-offset y-offset]
  (= p1 p2))


(defn valid-patterns
  [patterns under-pattern [x-offset y-offset]]
  (filterv (fn [over-pattern]
             (overlaps? under-pattern over-pattern x-offset y-offset))
           patterns))


(defn build-overlap-index
  [patterns]
  (vec (for [pattern patterns]
         (vec (for [offset (build-offsets pattern-width pattern-height)]
                [offset (valid-patterns patterns pattern offset)])))))


;;
(defn finished?
  [coefficient-matrix]
  false)


(defn output
  [coefficient-matrix]
  (.log js/console coefficient-matrix))


;; find lowest entropy (# of possibilities?)
;; if contradiction, bail out
;; if all cells at entropy 0, we're done!
;;  else, choose a pattern by a random sample
;;   (weighted by the pattern frequency in the source data)
;    set the boolean array in this cell to false, except
;     for the chosen pattern
(defn observe
  [coefficient-matrix]
  coefficient-matrix)


(defn propagate
  [coefficient-matrix]
  coefficient-matrix)


(defn run
  [sample]
  (let [patterns (patterns-from-sample sample)
        overlap-index (build-overlap-index patterns)]
    (loop [coefficient-matrix {}]
      (if (finished? coefficient-matrix)
        coefficient-matrix
        (recur (-> coefficient-matrix
                   (observe)
                   (propagate)))))))


;;


(def red-maze-sample
  [[:white :white :white :white]
   [:white :black :black :black]
   [:white :black :red :black]
   [:white :black :black :black]])


(defcard red-maze-sample red-maze-sample)


(defcard-rg red-maze-sample-render
  (render-pattern red-maze-sample))


(defcard red-maze-patterns
  (patterns-from-sample red-maze-sample))


(defcard-rg red-maze-patterns-render
  (render-patterns (patterns-from-sample red-maze-sample)))


(defcard offsets
  (build-offsets pattern-width pattern-height))


(defcard red-maze-overlap-index-first
   (nth (build-overlap-index (patterns-from-sample red-maze-sample))
        0))


(defcard-rg red-maze-overlap-first-render
  (render-overlaps
   (first (patterns-from-sample red-maze-sample))
   (nth (build-overlap-index (patterns-from-sample red-maze-sample))
        0)))


(defcard red-maze-overlap-index
  (build-overlap-index (patterns-from-sample red-maze-sample)))
