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


;; TODO get rid of these globals
(def pattern-width 2)
(def pattern-height 2)


(defn get-2d
  [arr [x y]]
  (-> arr
      (nth y)
      (nth x)))


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


;; TODO periodic input (patterns can wrap sample)
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
  (reduce-kv (fn [s idx over-pattern]
               (if (agrees? under-pattern over-pattern x-offset y-offset)
                 (conj s idx)
                 s))
             #{}
             patterns))


(defn build-overlap-index
  [patterns offsets]
  (vec (for [pattern patterns]
         (into {} (for [offset offsets]
                    [offset (valid-patterns patterns pattern offset)])))))


(defn build-wave
  [w h num-patterns]
  (vec (repeat h
               (vec (repeat w
                            (vec (repeat num-patterns true)))))))


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
          (let [e (cell-entropy (-> wave (nth y) (nth x)))]
            (recur (inc x) y (conj entropies {:x x
                                              :y y
                                              :entropy e}))))))))


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


(defn collapse-cell
  [cell]
  (assoc
   (vec (repeat (count cell) false))
   (rand-nth (reduce-kv
              (fn [r pattern-idx y?]
                (if y?
                  (conj r pattern-idx)
                  r))
              []
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
      (let [cell (rand-nth (:low-cells e-details))]
        [(update-in wave [(:y cell) (:x cell)] collapse-cell)
         [(:x cell) (:y cell)]]))))


(defn build-neighbor-offsets
  [offsets width height [x-pos y-pos]]
  (filter (fn [[x-offset y-offset]]
            (let [x (+ x-pos x-offset)
                  y (+ y-pos y-offset)]
              (and (>= x 0)
                   (< x width)
                   (>= y 0)
                   (< y height))))
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


(defn propagate-next
  [initial-wave constraints initial-frontier offsets width height]
  (let [C1-pos (peek initial-frontier)
        C1 (get-2d initial-wave C1-pos)
        neighbor-offsets (build-neighbor-offsets offsets width height C1-pos)]
    (loop [wave initial-wave
           frontier (pop initial-frontier)
           i 0]
      (if (< i (count neighbor-offsets))
        (let [offset (nth neighbor-offsets i)
              [x-offset y-offset] offset
              [C1-x C1-y] C1-pos
              C2-x (+ C1-x x-offset)
              C2-y (+ C1-y y-offset)
              wave' (update-in
                     wave
                     [C2-y C2-x]
                     (fn [C2]
                       (constrained-neighbor constraints C1 C2 offset)))]
          (if (= wave' wave)
            (recur wave frontier (inc i))
            (recur wave' (conj frontier [C2-x C2-y]) (inc i))))
        [wave frontier]))))


(defn propagate
  [initial-wave constraints pos offsets width height]
  (loop [wave initial-wave
         frontier #queue [pos]]
    (if (empty? frontier)
      wave
      (let [[wave' frontier']
            (propagate-next wave constraints frontier offsets width height)]
        (recur wave' frontier')))))


(defn run
  [{:keys [patterns pattern-width pattern-height width height]}]
  (let [offsets (build-offsets pattern-width pattern-height)
        constraints (build-overlap-index patterns offsets)
        initial-wave (build-wave width height (count patterns))]
    (loop [wave initial-wave]
      (let [[obs-wave observed-pos] (observe wave)]
        (if (= obs-wave wave)
          wave
          (recur (propagate obs-wave constraints observed-pos offsets width height)))))))


;;


(def sample
  [[:white :white :black :white :white :white]
   [:white :black :black :black :white :white]
   [:white :red :yellow :red :white :white]
   [:white :black :black :black :white :white]
   [:white :white :white :black :white :white]])
#_(defcard sample sample)


(defcard-rg full-render
  (let [patterns (patterns-from-sample sample)]
    (render-wave
     (run {:patterns patterns
           :pattern-width 2
           :pattern-height 2
           :width 20
           :height 20})
     patterns)))


(defcard-rg sample-render
  (render-pattern sample))


(def patterns (patterns-from-sample sample))
#_(defcard patterns patterns)
(defcard-rg patterns-render
  (render-patterns patterns))


(def offsets (build-offsets pattern-width pattern-height))
#_(defcard offsets offsets)


(def overlap-index (build-overlap-index patterns offsets))
(defcard overlap-index overlap-index)
(defcard-rg overlap-index-render
  (render-overlap-index patterns overlap-index))


#_(def wave (build-wave 5 5 (count patterns)))
#_(defcard wave wave)
#_(defcard-rg wave-render
    (render-wave wave patterns))

#_(defonce test-wave (reagent.core/atom wave))
#_(defn test-render [] (render-wave @test-wave patterns))
#_(defcard-rg stepped-test
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
