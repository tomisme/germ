(ns gateworld.dev.core
  (:require
   [gateworld.dev.wfc]
   [devcards.core]
   [re-frame.core :as rf]
   [gateworld.client.core]
   [gateworld.client.lang]
   [gateworld.client.views])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn ui-test-component
  []
  [:div
   [gateworld.client.core/ui-component]
   [:div {:style {:margin 10
                  :padding 10
                  :border "1px dashed"}}
    [:span
     "Active view: " @(rf/subscribe [:active-view])]
    [:div {:style {:margin-top 10}}
     [:button {:on-click #(rf/dispatch [:initialise])}
      "Init"]]
    [:div {:style {:margin-top 10}}
     [:button {:on-click #(rf/dispatch [:start-conflict-practise])}
      "Start Conflict"]]]])


(defcard-rg ui
  [ui-test-component])


(devcards.core/start-devcard-ui!)
