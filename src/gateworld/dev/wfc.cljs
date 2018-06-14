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


(defn render-overlap
  [pattern overlap-index]
  [:div {:style {:display "flex"
                 :margin 5}}
   [:div {:style {:margin 5}}
    (render-pattern pattern)]
   (into [:div]
         (for [[[offset-x offset-y] overlapping-patterns] overlap-index]
           [:div {:style {:display "flex"
                          :border "1px solid grey"
                          :padding 5
                          :margin 5}}
            [:div (str "(" offset-x "," offset-y ")")]
            (into [:div {:style {:display "flex"
                                 :flex-wrap "wrap"}}]
                  (for [p overlapping-patterns]
                    [:div {:style {:margin 5}}
                     (render-pattern p)]))]))])


(defn render-overlap-index
  [patterns overlap-index]
  (into [:div]
        (map-indexed (fn [idx pattern]
                       [:div
                        (render-overlap pattern (nth overlap-index idx))])
                     patterns)))

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


;; TODO periodic input (pattern can wrap sample)
(defn get-pattern-at
  [sample x y]
  (vec (for [i (range pattern-height)]
          (subvec
           (nth sample (+ y i))
           x
           (+ x pattern-width)))))


;; TODO reflections/rotations
;; TODO weighted by num of occurrence
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
          (vec (distinct patterns))
          (recur (inc x) y (conj patterns (get-pattern-at sample x y))))))))


(defn agrees?
  [p1 p2 x-offset y-offset]
  (let [p1-start-x (if (pos? x-offset) x-offset 0)
        p1-start-y (if (pos? y-offset) y-offset 0)
        overlap-width (- pattern-width (.abs js/Math x-offset))
        overlap-height (- pattern-height (.abs js/Math y-offset))
        p1-end-x (+ p1-start-x overlap-width)
        p1-end-y (+ p1-start-y overlap-height)]
    (loop [p1-x p1-start-x
           p1-y p1-start-y]
      (if (= p1-x p1-end-x)
        (recur p1-start-x (inc p1-y))
        (if (= p1-y p1-end-y)
          true
          (if (= (-> p1
                     (nth p1-y)
                     (nth p1-x))
                 (-> p2
                     (nth (+ p1-y (* -1 y-offset)))
                     (nth (+ p1-x (* -1 x-offset)))))
            (recur (inc p1-x) p1-y)
            false))))))


(defn valid-patterns
  [patterns under-pattern [x-offset y-offset]]
  (filterv (fn [over-pattern]
             (agrees? under-pattern over-pattern x-offset y-offset))
           patterns))


(defn build-overlap-index
  [patterns offsets]
  (vec (for [pattern patterns]
         (vec (for [offset offsets]
                [offset (valid-patterns patterns pattern offset)])))))


(defn finished?
  [coefficient-matrix])


(defn output
  [coefficient-matrix])


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
  [{:keys [sample output-w output-h]}]
  (let [patterns (patterns-from-sample sample)
        offsets (build-offsets pattern-width pattern-height)
        overlap-index (build-overlap-index patterns offsets)]
    (loop [coefficient-matrix {}]
      (if (finished? coefficient-matrix)
        coefficient-matrix
        (recur (-> coefficient-matrix
                   (observe)
                   (propagate)))))))


;;


(def sample
  [[:white :white :white :white]
   [:white :black :black :black]
   [:white :black :red :black]
   [:white :black :black :black]])
(defcard sample sample)
(defcard-rg sample-render
  (render-pattern sample))


(def patterns (patterns-from-sample sample))
(defcard patterns patterns)
(defcard-rg patterns-render
  (render-patterns patterns))


(def offsets (build-offsets pattern-width pattern-height))
(defcard offsets offsets)


(def overlap-index (build-overlap-index patterns offsets))
(defcard-rg overlap-index-render
  (render-overlap-index patterns overlap-index))
