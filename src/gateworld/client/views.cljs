(ns gateworld.client.views
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(def views
  #{:intro
    :conflict
    :map
    :editor
    :settings})
(defcard views views)
