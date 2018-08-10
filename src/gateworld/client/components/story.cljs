(ns gateworld.client.components.story
  (:require
   [re-frame.core :as rf])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn story-item-el
  [x]
  [:li x])


(defn story-el
  [items]
  [:div {:style {:min-width 200
                 :border "1px solid"
                 :margin-right 10
                 :padding 10}}
   (into [:ul {:style {:padding-left 15}}]
         (map story-item-el items))])


(defn story-component
  []
  (story-el @(rf/subscribe [:story-items])))


;;


(def example-story-1
  ["Hello"
   "World"])


(defcard-rg example-story-1
  (story-el example-story-1))
