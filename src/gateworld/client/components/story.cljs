(ns gateworld.client.components.story
  (:require
   [re-frame.core :as rf]))


(defn story-component
  []
  [:div {:style {:min-width 200
                 :border "1px solid"
                 :margin-right 10
                 :padding 10}}
   (into [:ul {:style {:padding-left 15}}]
         (map (fn [x] [:li x])
              @(rf/subscribe [:story-items])))
   [:div {:style {:margin-top 20}}
    "What next?"]
   (into [:ul {:style {:padding-left 15}}]
         (map (fn [x] [:li x])
              @(rf/subscribe [:next-actions])))])
