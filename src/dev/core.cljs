(ns dev.core
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [re-frame.core :as rf]

   [dev.animals]
   [dev.rolypoly.bedmap]
   [dev.dream.scratch]
   [dev.frog.pond]
   [dev.gran]
   [dev.images]
   [dev.masonry]
   [dev.wfc]

   [gateworld.client.core]
   [gateworld.client.views]))

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

(def ui)
(defcard-rg ui
  [ui-test-component])


(devcards.core/start-devcard-ui!)
