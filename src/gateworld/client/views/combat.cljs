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
                   :padding 10
                   :margin 5
                   :border "1px solid"}
           :on-click on-click}
     [:div name]
     (when (or attack toughness)
       [:div attack "/" toughness])]))


(defn field-component
  [char-idx]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map-indexed (fn [perm-idx card]
                       (field-permanent-el char-idx perm-idx card))
                     @(rf/subscribe [:field-cards char-idx]))))


(defn hand-component
  []
  [:div]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map field-permanent-el @(rf/subscribe [:hand-cards 0]))))


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
