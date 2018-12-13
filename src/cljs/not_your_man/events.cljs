(ns not-your-man.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [not-your-man.db :as db])
  (:require-macros [not-your-man.macros :refer [read-file]]))

(defonce query
  (read-file "resources/query.rq"))

(re-frame/reg-event-db
  ::change-subject
  (fn [db [_ subject]]
    (assoc db :subject subject)))

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

(re-frame/reg-event-fx
  ::wikidata-query
  (fn [{:keys [db]} _]
    {:db (assoc db :loading? true) 
     :http-xhrio {:method          :get
                  :uri             "https://query.wikidata.org/sparql"
                  :timeout         15000
                  :params          {:query (beat-query-caching query)}
                  :response-format (ajax/json-response-format {:keywords? true}) 
                  :on-success      [::query-results]
                  :on-failure      [::query-error]}}))

(defmulti parse-sparql-value :type)

(defmethod parse-sparql-value "literal"
  [{:keys [value]}]
  value)

(defn parse-sparql-binding
  [b]
  (->> b
       (map (juxt key (comp parse-sparql-value val)))
       (into {})))

(defn parse-sparql-results
  [{{:keys [bindings]} :results}]
  (map parse-sparql-binding bindings))

(def parse-sparql-results-interceptor
  (re-frame/->interceptor :id ::parse-sparql-results
                          :before (fn [context]
                                    (update-in context [:coeffects :event 1] parse-sparql-results))))

(re-frame/reg-event-db
  ::query-results
  [parse-sparql-results-interceptor]
  (fn [db [_ [{:keys [label]} & _ :as results]]]
    (-> db
        (dissoc :loading?)
        (assoc :label label)
        (assoc :not-your-man (map :notyourman results)))))

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
     :dispatch [::wikidata-query]}))

(re-frame/reg-event-db
  ::hide-error
  (fn [db _]
    (dissoc db :error)))
