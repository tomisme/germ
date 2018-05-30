(ns gateworld.dev.core
  (:require
   [devcards.core]
   [re-frame.core :as rf]
   [gateworld.client.core]
   [gateworld.client.db]
   [gateworld.client.lang]
   [gateworld.client.views]
   [gateworld.client.views.combat]
   [gateworld.client.views.editor]
   [gateworld.client.views.intro]
   [gateworld.client.views.map]
   [gateworld.rules.core]
   [gateworld.rules.effects])
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
     [:button {:on-click #(rf/dispatch [:start-combat-practise])}
      "Start Combat"]]]])


(defcard-rg ui
  [ui-test-component])


(devcards.core/start-devcard-ui!)
