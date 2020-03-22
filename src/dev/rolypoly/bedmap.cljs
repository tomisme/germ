(ns dev.rolypoly.bedmap
  (:require
   [reagent.core :as rg]
   [httpurr.client.xhr :as xhr]
   [promesa.core :as p]
   [cuerdas.core :as str])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))
(defn d [x] (js/console.log x) x)

(defonce beds
  (rg/atom {}))

(defn decode
  [response]
  (update response :body #(js->clj (js/JSON.parse %))))

(defn get!
  [url]
  (p/then (xhr/get url) decode))

(p/then (get! "http://localhost:3002/api/beds")
        (fn [response]
          (reset! beds (-> response
                           :body))))

(def bedmap)
(defcard bedmap #_beds)

(defn bedmap-component [bed-px]
  (into [:div {:style {:position "absolute"}}]
        (for [x @beds]
          (let [bay (get x "bay")
                run (get x "run")
                bed (get x "bed")
                plant (get x "mostRecentPlanting")]
            [:div {:style {:position "absolute"
                           :left (+ (* (- 9 bay) bed-px 7)
                                    (* (- 6 run) bed-px))
                           :top (+ 200 (* (- 6 bed) bed-px))
                           :cursor "pointer"
                           :font-size (* bed-px 0.75)}
                   :on-click #(js/window.alert plant)}
             (if plant
               (condp #(str/includes? %2 %1) plant
                 "COVER CROP" "🌳"
                 "LETTUCE" "🐌"
                 "KOHLRABI" "👽"
                 "EGGPLANT" "🍆"
                 "BEETROOT" "🎵"
                 "🌱")
               "⚠")]))))

; (def bedmap-ui)
; (defcard-rg bedmap-ui
;   [bedmap-component 15])

(def bedmap-ui2)
(defcard-rg bedmap-ui2
  [bedmap-component 40])
