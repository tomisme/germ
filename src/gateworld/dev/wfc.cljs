(ns gateworld.dev.wfc
  (:require
   [reagent.core])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn d [x] (let [_ (js/console.log x)] x))


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
  [patterns pattern overlap-index]
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
                  (for [idx overlapping-patterns
                        :let [p (nth patterns idx)]]
                    [:div {:style {:margin 5}}
                     (render-pattern p)]))]))])


(defn render-overlap-index
  [patterns overlap-index]
  (into [:div {:style {:display "flex"
                       :flex-wrap "wrap"}}]
        (map-indexed (fn [idx pattern]
                       [:div
                        (render-overlap patterns pattern (nth overlap-index idx))])
                     patterns)))


(defn render-wave
  [wave patterns]
  (let [grid (for [row wave]
               (for [cell row]
                 (let [num-valid (get (frequencies cell) true)]
                   {:num num-valid
                    :color (if (not= num-valid 1)
                             nil
                             (loop [i 0]
                               (if (nth cell i)
                                 (first (first (nth patterns i)))
                                 (recur (inc i)))))})))]
    (into [:div]
          (for [row grid]
            (into [:div {:style {:display "flex"}}]
                  (for [cell row]
                    [:div {:style {:width 32
                                   :height 32
                                   :background-color (or (:color cell)
                                                         "yellow")}}
                     (if-not (:color cell)
                      [:div {:style {:height "100%"
                                     :display "flex"}}
                       [:div {:style {:display "flex"
                                      :width "100%"
                                      :justify-content "center"
                                      :align-items "center"}}
                        [:span (str (:num cell))]]])]))))))


;;


(defn get-2d
  [arr [x y]]
  (-> arr (nth y) (nth x)))


(defn update-2d
  [arr [x y] f]
  (update-in arr [y x] f))


;;


(defn build-offsets
  [[width height]]
  (let [min-x (- (dec width))
        min-y (- (dec height))
        max-x (dec width)
        max-y (dec height)]
   (loop [x min-x
          y min-y
          offsets []]
     (if (> x max-x)
       (recur min-x (inc y) offsets)
       (if (> y max-y)
         offsets
         (recur (inc x) y (conj offsets [x y])))))))


;; TODO periodic input (patterns can wrap sample)
(defn get-pattern-at
  [sample x y w h]
  (vec (for [i (range h)]
          (subvec
           (nth sample (+ y i))
           x
           (+ x w)))))


;; TODO reflections/rotations
;; TODO weighted by frequency
(defn patterns-from-sample
  [sample pattern-size]
  (let [[pattern-width pattern-height] pattern-size
        sample-width (count (first sample))
        sample-height (count sample)]
    (loop [x 0
           y 0
           patterns []]
      (if (> x (- sample-width pattern-width))
        (recur
         0
         (inc y)
         patterns)
        (if (> y (- sample-height pattern-height))
          (vec (distinct patterns))
          (recur
           (inc x)
           y
           (conj patterns
                 (get-pattern-at sample x y pattern-width pattern-height))))))))


(defn agrees?
  [p1 p2 x-offset y-offset [pattern-width pattern-height]]
  (let [p1-start-x (if (pos? x-offset) x-offset 0)
        p1-start-y (if (pos? y-offset) y-offset 0)
        overlap-width (- pattern-width (.abs js/Math x-offset))
        overlap-height (- pattern-height (.abs js/Math y-offset))
        p1-end-x (+ p1-start-x overlap-width)
        p1-end-y (+ p1-start-y overlap-height)]
    ;; check each overlapping cell pair
    (loop [p1-x p1-start-x
           p1-y p1-start-y]
      (if (= p1-x p1-end-x)
        (recur p1-start-x (inc p1-y))
        (if (= p1-y p1-end-y)
          ;; all pairs matched
          true
          (if (= (get-2d p1 [p1-x p1-y])
                 (get-2d p2 [(+ p1-x (* -1 x-offset))
                             (+ p1-y (* -1 y-offset))]))
            (recur (inc p1-x) p1-y)
            ;; a pair didn't match
            false))))))


(defn valid-patterns
  [patterns under-pattern [x-offset y-offset] pattern-size]
  (reduce-kv (fn [s idx over-pattern]
               (if (agrees? under-pattern over-pattern x-offset y-offset pattern-size)
                 (conj s idx)
                 s))
             #{}
             patterns))


(defn build-constraints
  [patterns offsets pattern-size]
  (vec (for [pattern patterns]
         (into {} (for [offset offsets]
                    [offset (valid-patterns patterns pattern offset pattern-size)])))))


(defn initialize-wave
  [[w h] num-possibilities]
  (vec (repeat h
               (vec (repeat w
                            (vec (repeat num-possibilities true)))))))


;; TODO use freq info
(defn cell-entropy
  [cell]
  (reduce
   (fn [total valid?]
     (if valid?
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
          (recur (inc x) y (conj entropies {:x x
                                            :y y
                                            :entropy (cell-entropy (get-2d wave [x y]))})))))))


(defn analyze-entropies
  [entropies]
  (reduce
   (fn [details cell]
     (let [low-num (:low-num details)
           entropy (:entropy cell)]
       (cond
         (or (:contradiction details)
             (= entropy 0))
         details

         (< entropy 0)
         (assoc details :contradiction true)

         (or (not low-num) (< entropy low-num))
         (-> details
             (assoc :low-num entropy)
             (assoc :low-cells (list cell)))

         (= entropy low-num)
         (update details :low-cells conj cell)

         (> entropy low-num)
         details

         :else
         (let [_ (throw "?")]
           details))))
   {:low-num nil
    :low-cells '()
    :contradiction false}
   entropies))


;; pick a random true and set everything else to false
;; TODO weighted by frequency info?
(defn observe-cell
  [cell]
  (assoc
   (vec (repeat (count cell) false))
   (rand-nth (reduce-kv
              (fn [col idx y?]
                (if y?
                  (conj col idx)
                  col))
              '()
              cell))
   true))


(defn observe
  [wave]
  (let [e-details (analyze-entropies (wave-entropies wave))]
    (cond
      (:contradiction e-details)
      (let [_ (js/console.log "contradiction!")]
        [wave nil])

      (empty? (:low-cells e-details))
      (let [ _ (js/console.log "done!" e-details)]
        [wave nil])

      :else
      (let [{:keys [x y]} (rand-nth (:low-cells e-details))
            wave' (update-2d wave [x y] observe-cell)]
        [wave' [x y]]))))


;; TODO should neighbors wrap output?
(defn build-neighbor-offsets
  [offsets [output-width output-height] [x-pos y-pos]]
  (filter (fn [[x-offset y-offset]]
            (let [x (+ x-pos x-offset)
                  y (+ y-pos y-offset)]
              (and (>= x 0)
                   (< x output-width)
                   (>= y 0)
                   (< y output-height))))
          offsets))


(defn constrained-neighbor
  [constraints C1 C2 offset]
  (vec (map-indexed
        (fn [P2-idx P2-valid?]
          (if (not P2-valid?)
            false
            (reduce-kv
             (fn [P2-still-valid? P1-idx P1-valid?]
               (if P2-still-valid?
                 true
                 (if (not P1-valid?)
                   P2-still-valid?
                   (contains? (get-in constraints [P1-idx offset])
                              P2-idx))))
             false
             C1)))
        C2)))


(defn constrain-neighbor
  [wave constraints C1 C2-pos offset]
  ;; reduce possibility space of C2
  (update-2d wave C2-pos #(constrained-neighbor constraints C1 % offset)))


(defn propagate-next
  [initial-wave constraints initial-frontier offsets size]
  (let [;; take C1 off of the queue
        C1-pos (peek initial-frontier)
        C1 (get-2d initial-wave C1-pos)
        popped-frontier (pop initial-frontier)]
    (reduce
     (fn [[wave frontier] offset]
       (let [[x-offset y-offset] offset
             [C1-x C1-y] C1-pos
             C2-pos [(+ C1-x x-offset) (+ C1-y y-offset)]
             ;; C1 has been affected by an observation/propagation
             ;; constrain neighbor (C2) against this new C1
             wave' (constrain-neighbor wave constraints C1 C2-pos offset)
             ;; propagate any changes to C2 to its neighbors
             frontier' (if (= wave' wave)
                         frontier
                         (conj frontier C2-pos))]
         [wave' frontier']))
     [initial-wave popped-frontier]
     (build-neighbor-offsets offsets size C1-pos))))


(defn propagate
  [initial-wave constraints start-pos offsets size]
  (loop [wave initial-wave
         ;; constrain in a breadth-first search
         ;; each node constrains each of its neighbors
         ;; adding neighbors that change to the end of the queue
         frontier #queue [start-pos]]
    (if (empty? frontier)
      wave
      (let [[wave' frontier']
            (propagate-next wave constraints frontier offsets size)]
        (recur wave' frontier')))))


(defn run
  [{:keys [patterns pattern-size width height]}]
  (let [offsets (build-offsets pattern-size)
        ;; each pattern can only overlap other patterns
        ;; in ways found in the source image
        constraints (build-constraints patterns offsets pattern-size)
        ;; initially, any pattern could be at any location
        initial-wave (initialize-wave [width height] (count patterns))]
    (loop [wave initial-wave]
      ;; select a cell from those with the lowest entropy
      ;; and reduce it to a single possibility
      (let [[wave' pos] (observe wave)]
        ;; if no cell could be selected, we're done!
        (if-not pos
          wave
          ;; walk over possibly affected cells,
          ;; reduce possibilities until whole wave is stable again
          ;; or there is a contradiction!
          (recur (propagate wave' constraints pos offsets [width height])))))))


;; devcards


(def test-pattern-size [2 2])

(def sample
  [[:white :white :white :white :white :white]
   [:white :black :black :black :black :white]
   [:white :orange :green :white :white :white]
   [:white :black :black :black :white :white]
   [:white :white :white :black :white :white]])
(defcard sample sample)

(defcard-rg sample-render
  (render-pattern sample))

(defcard-rg full-render
  (let [patterns (patterns-from-sample sample test-pattern-size)]
    (render-wave
     (run {:patterns patterns
           :pattern-size test-pattern-size
           :width 16
           :height 16})
     patterns)))

(def patterns (patterns-from-sample sample test-pattern-size))
(defcard patterns patterns)
(defcard-rg patterns-render
  (render-patterns patterns))

(def offsets (build-offsets test-pattern-size))
(defcard offsets offsets)

(def overlap-index
  (build-constraints patterns offsets test-pattern-size))
(defcard overlap-index overlap-index)
(defcard-rg overlap-index-render
  (render-overlap-index patterns overlap-index))

(def wave (initialize-wave [5 5] (count patterns)))
(defcard wave wave)
(defcard-rg wave-render
  (render-wave wave patterns))

(defonce test-wave (reagent.core/atom wave))
(defn test-render [] (render-wave @test-wave patterns))
(defcard-rg stepped-test
  [:div
   [test-render]
   [:div
    [:button {:on-click #()}
     "observe"]
    [:button {:on-click #()}
     "propagate"]]]
  test-wave
  {:history true
   :inspect-data true})
