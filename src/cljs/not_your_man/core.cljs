(ns not-your-man.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [not-your-man.events :as events]
            [not-your-man.views :as views]
            [not-your-man.config :as config]))
   
(defn dev-setup
  []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel] (.getElementById js/document "app")))

(defn ^:export init
  []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
