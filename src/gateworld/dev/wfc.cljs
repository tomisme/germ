(ns gateworld.dev.wfc
  (:require
   [reagent.core])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn d [x] (let [_ (js/console.log x)] x))


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


(defn render-wave
  [wave patterns]
  (let [grid (vec (for [row wave]
                    (vec (for [cell row]
                           (let [freq (frequencies cell)
                                 num-valid (get freq true)]
                             {:num num-valid
                              :color (if (not= num-valid 1)
                                       nil
                                       (loop [i 0]
                                         (if (nth cell i)
                                           (first (first (nth patterns i)))
                                           (recur (inc i)))))})))))]
    (into [:div]
          (for [row grid]
            (into [:div {:style {:display "flex"}}]
                  (for [cell row]
                    [:div {:style {:width 25
                                   :height 25
                                   :border "1px solid grey"
                                   :margin 1
                                   :background-color (or (:color cell)
                                                         "yellow")}}
                     (if-not (:color cell) (str (:num cell)))]))))))


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
;; TODO weighted by (frequencies)
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


(defn build-matrix
  [w h num-patterns]
  (vec (repeat h
               (vec (repeat w
                            (vec (repeat num-patterns true)))))))


;; cell: vector
;;   idx = pattern idx
;;   val = boolean - still valid option
;; ret: sum of freq of true pattern ids in source
(defn entropy
  [cell]
  (reduce
   (fn [total valid?]
     (if valid?
       ;; TODO freq info
       (+ 1 total)
       total))
   -1
   cell))


(defn wave-entropies
  [wave]
  (let [max-y (dec (count wave))
        max-x (dec (count (first wave)))]
    (loop [x 0
           y 0
           entropies '()]
      (if (> y max-y)
        entropies
        (if (> x max-x)
          (recur 0 (inc y) entropies)
          (let [e (entropy (-> wave (nth y) (nth x)))]
            (recur (inc x) y (conj entropies {:x x
                                              :y y
                                              :entropy e}))))))))


(defn analyze-entropies
  [entropies]
  (reduce
   (fn [r cell]
     (let [low-num (:low-num r)
           entropy (:entropy cell)]
       (cond
         (:contradiction r) r

         (< entropy 0)
         (assoc r :contradiction true)

         (= entropy 0) r

         (or (not low-num) (< entropy low-num))
         (-> r
             (assoc :low-num entropy)
             (assoc :low-cells (list cell)))

         (= entropy low-num)
         (update-in r [:low-cells] conj cell)

         :else r)))
   {:low-num nil
    :low-cells '()
    :contradiction false}
   entropies))


(defn resolve-cell
  [wave x y]
  (update-in wave [y x] (fn [cell]
                          (assoc
                           (vec (repeat (count cell) false))
                           (rand-nth (reduce-kv
                                      (fn [r pattern-idx y?]
                                        (if y?
                                          (conj r pattern-idx)
                                          r))
                                      []
                                      cell))
                           true))))


(defn observe
  [wave]
  (let [e-details (analyze-entropies (wave-entropies wave))]
    (cond
      (:contradiction e-details)
      (let [_ (js/console.log "contradiction!" wave)]
        wave)

      (empty? (:low-cells e-details))
      (let [ _ (js/console.log "done!" wave)]
        wave)

      :else
      (let [cell (rand-nth (:low-cells e-details))]
        (resolve-cell wave (:x cell) (:y cell))))))


(defn propagate
  [wave]
  wave)


(defn run
  [{:keys [sample output-w output-h]}]
  (let [patterns (patterns-from-sample sample)
        offsets (build-offsets pattern-width pattern-height)
        overlap-index (build-overlap-index patterns offsets)]))
    ; (loop [wave (build-matrix output-w output-h (count patterns))]
    ;   (if (finished? wave)
    ;     wave
    ;     (recur (-> wave
    ;                (observe)
    ;                (propagate)))))))


;;


(def o-w 4)
(def o-h 3)


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


(def wave (build-matrix o-w o-h (count patterns)))
(defcard wave wave)
(defcard-rg wave-render
  (render-wave wave patterns))


(defonce test-wave (reagent.core/atom wave))
(defn test-render [] (render-wave @test-wave patterns))
(defcard-rg test
  [:div
   [test-render]
   [:div
    [:button {:on-click #(swap! test-wave observe)}
     "observe"]
    [:button {:on-click #(swap! test-wave propagate)}
     "propagate"]]]
  test-wave
  {:history true
   :inspect-data true})
