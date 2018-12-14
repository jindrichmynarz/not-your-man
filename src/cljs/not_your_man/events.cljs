(ns not-your-man.events
  (:require [re-frame.core :as re-frame :refer [->interceptor]]
            [ajax.core :as ajax]
            [not-your-man.db :as db])
  (:require-macros [not-your-man.macros :refer [read-file]]))

(def default-query
  (read-file "resources/query.rq"))

; ----- Private functions -----

(defn- rand-str
  [length]
  (->> (repeatedly (partial rand-int 26))
       (map (comp char (partial + 65)))
       (take length)
       (apply str)))

(defn- beat-query-caching
  "Beat SPARQL query caching by prefixing `query` with a random string."
  [query]
  (str "# " (rand-str (rand-int 10)) "\n" query))

(defn parse-sparql-binding
  [b]
  (->> b
       (map (juxt key (comp :value val)))
       (into {})))

(defn parse-sparql-results
  [{{:keys [bindings]} :results}]
  (map parse-sparql-binding bindings))

; ----- Events & effect handlers -----

(re-frame/reg-event-db
  ::change-subject
  (fn [db [_ subject]]
    (assoc db :subject subject)))

(re-frame/reg-event-fx
  ::sparql-query
  (fn [{:keys [db]} & {:keys [endpoint on-success query timeout]
                       :or {endpoint "https://query.wikidata.org/sparql"
                            on-success [::query-results]
                            query default-query
                            timeout 15000}}]
    {:db (assoc db :loading? true) 
     :http-xhrio {:method          :get
                  :uri             endpoint
                  :timeout         timeout
                  :params          {:query (beat-query-caching query)}
                  :response-format (ajax/json-response-format {:keywords? true}) 
                  :on-success      on-success
                  :on-failure      [::query-error]}}))

(def parse-sparql-results-interceptor
  (->interceptor :id ::parse-sparql-results
                 :before (fn [context]
                           (update-in context [:coeffects :event 1] parse-sparql-results))))

(re-frame/reg-event-db
  ::query-results
  [parse-sparql-results-interceptor]
  (fn [db [_ [{:keys [label]} & _ :as results]]]
    (-> db
        (dissoc :loading?)
        (assoc :label label
               :not-your-man results))))

(re-frame/reg-event-db
  ::query-error
  (fn [db [_ result]]
    (-> db
        (dissoc :loading?)
        (assoc :error result))))

(re-frame/reg-event-fx
  ::initialize-db
  (fn [_ _]
    {:db db/default-db
     :dispatch [::sparql-query]}))

(re-frame/reg-event-db
  ::hide-error
  (fn [db _]
    (dissoc db :error)))
