(ns gateworld.client.views
  (:require
    [re-frame.db])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(def views
  #{:intro
    :combat
    :map
    :editor
    :settings})
(defcard views views)
