(ns gateworld.client.views.conflict
  (:require
   [re-frame.core :as rf]
   [gateworld.client.components.story :refer [story-component]]))


;; reagent elements


;; draggable?
;; selectable?
(defn field-permanent-el
  [char-idx perm-idx {:keys [name attack toughness]}]
  (let [on-click #(rf/dispatch [:conflict/sacrifice-permanent
                                char-idx
                                perm-idx])]
    [:div {:style {:cursor "pointer"
                   :background "rgb(240,240,240)"
                   :padding 12
                   :margin 5}
           :on-click on-click}
     [:div {:style {:font-size 20}}
      name]
     (when (or attack toughness)
       [:div {:style {:display "flex"
                      :justify-content "flex-end"}}
        [:span attack "/" toughness]])]))


;; could these represent actions taken by your character?
;; selectable?
(defn hand-card-el
  [x]
  [:div {:style {:padding 12
                 :margin 5
                 :border "3px dashed"}}
   (:name x)])


;; re-frame components


(defn field-component
  [char-idx]
  [:div
   [:div
    (str char-idx) " field"]
   (into [:div {:style {:display "flex"
                        :margin 10
                        :padding 10
                        :border-left "1px solid"
                        :border-bottom "1px solid"}}]
         (map-indexed (fn [perm-idx card]
                        (field-permanent-el char-idx perm-idx card))
                      @(rf/subscribe [:field-cards char-idx])))])


(defn hand-component
  []
  [:div]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map hand-card-el @(rf/subscribe [:hand-cards 0]))))


(defn main-component
  []
  [:div {:style {:padding 10
                 :border "1px solid"}}
   [field-component 1]
   [field-component 0]
   [hand-component]])


(defn conflict-view
  []
  [:div {:style {:border "1px solid"
                 :padding 10
                 :display "flex"}}
   [story-component]
   [main-component]])
