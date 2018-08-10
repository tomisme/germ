(ns gateworld.client.components.card
  (:require
   [reanimated.core :as anim]
   [reagent.core :as rg])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))
(defn d [x] (let [_ (js/console.log x)] x))


(def card-width 200)


(defn card-el
  [card idx active? selected?]
  (let [shadow "0 8px 18px rgba(0,0,0,0.2), 0 8px 8px rgba(0,0,0,0.2)"]
    [:div {:style {:width card-width
                   :height (* card-width 1.5)
                   :background-color "green"
                   :border "10px solid white"
                   :box-shadow shadow
                   :display "flex"
                   :flex-direction "column"}}
     [:div {:style {:display "flex"
                    :justify-content "center"
                    :margin-top 10}}
      [:img {:src (if (even? idx)
                    "icons/noun_1653410_cc.svg"
                    "icons/noun_1710142_cc.svg")
             :width 100}]]]))


(def spin-atom (rg/atom 1))
(def hovered-card-idx (rg/atom nil))
(def selected-card-idx (rg/atom nil))
(def spin-spring (anim/spring spin-atom {:mass 1.5}))
(defn hand-of-cards-el
  [cards]
  (let [hand-width (* @spin-spring 500)
        hand-height 300
        spread-deg (* 60 @spin-spring)
        half-spread-deg (/ spread-deg 2)
        num-cards (count cards)
        deg-interval (/ spread-deg (dec num-cards))
        selection? @selected-card-idx
        hand-spacing-px (/ hand-width num-cards)]
    [:div {:style {:height 100
                   :background "red"}
           :on-mouse-over #(when-not selection?
                             (reset! spin-atom 1.4))
           :on-mouse-out #(do
                           ; (reset! hovered-card-idx nil)
                           (reset! spin-atom 1))}
     (into [:div {:style {:background "green"
                          :width hand-width
                          :height hand-height
                          ; :background "rgb(220,220,220)"
                          :position "absolute"
                          :top (+ 700 #_(* -50 @spin-spring))
                          :left 100}}]
           (map-indexed
            (fn [idx card]
              (let [spin @spin-spring
                    rtl? true
                    active? (= @hovered-card-idx idx)
                    selected? (= @selected-card-idx idx)
                    deg (if selected?
                          0
                          (- (* idx deg-interval) half-spread-deg))
                    left (* idx hand-spacing-px)
                    rotation-str (str "rotate(" deg "deg)")
                    scale-str (str "scale(" spin "," spin ")")
                    transform (str rotation-str scale-str)
                    top (if selected?
                          -400
                          (if (and active? (not selection?))
                            (* spin -120)
                            0))
                    z-index (if active?
                              (+ num-cards idx)
                              (if rtl?
                                (- num-cards idx)
                                idx))]
                [:div {:style {:position "absolute"
                               :opacity 0.5
                               :left left
                               :top top
                               :cursor "pointer"
                               ; :transition "top 0.2s ease"
                               :transform-origin "bottom"
                               :transform transform
                               :z-index z-index}
                       :on-mouse-over #(when (not active?)
                                        (reset! hovered-card-idx idx))
                       :on-click #(when (not selected?)
                                   (do
                                    (reset! selected-card-idx idx)))}
                 (when selected?
                   [:div {:style {:position "absolute"
                                  :background "blue"
                                  :cursor "pointer"
                                  :top -30
                                  :right -30
                                  :font-size "1.2rem"}
                          :on-click #(do
                                      (reset! hovered-card-idx nil)
                                      (reset! selected-card-idx nil))}
                    "X"])
                 (when (or selected? active?)
                   [:div {:style {:position "absolute"
                                  :top -30
                                  :font-size "1.2rem"}}
                    (if (even? idx)
                      "Green Gate"
                      "Blue Gate")])
                 (card-el card idx active? selected?)]))
            cards))]))


;;


(defcard-rg example-card-1
  (card-el {} 0 false false))


(defcard-rg example-hand-1
  [hand-of-cards-el [{} {} {} {} {}]])


(defcard-rg example-hand-2
  [hand-of-cards-el [{} {} {} {} {} {} {} {} {}]])
