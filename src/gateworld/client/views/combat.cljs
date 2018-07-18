(ns gateworld.client.views.combat
  (:require
   [re-frame.core :as rf]
   [gateworld.client.components.story :refer [story-component]]))


(defn field-permanent-el
  [char-idx perm-idx {:keys [name attack toughness]}]
  (let [on-click #(rf/dispatch [:combat/sacrifice-permanent
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


(defn field-component
  [char-idx]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map-indexed (fn [perm-idx card]
                       (field-permanent-el char-idx perm-idx card))
                     @(rf/subscribe [:field-cards char-idx]))))

(defn hand-card-el
  [x]
  [:div {:style {:padding 12
                 :margin 5
                 :border "3px dashed"}}
   (:name x)])


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
   [:hr]
   [field-component 0]
   [:hr]
   [:hr]
   [hand-component]])


(defn combat-component
  []
  [:div {:style {:border "1px solid"
                 :padding 10
                 :display "flex"}}
   [story-component]
   [main-component]])
