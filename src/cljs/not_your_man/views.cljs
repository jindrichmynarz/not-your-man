(ns not-your-man.views
  (:require [re-frame.core :refer [dispatch subscribe]]
            [re-com.core :as re-com]
            [not-your-man.events :as events]
            [not-your-man.config :as config]
            [not-your-man.subs :as subs]
            [goog.string :refer [format]]))

(defn loading-indicator
  []
  (let [loading? (subscribe [::subs/loading?])
        subject (subscribe [::subs/subject])]
    (when @loading?
      [re-com/h-box :children [[re-com/title :label (format "Loading your %s..." @subject)
                                             :level :level2]
                               [re-com/throbber :color "#000"]]])))

(defn hide-error
  []
  (dispatch [::events/hide-error]))

(defn error
  []
  (when-let [error-text @(subscribe [::subs/error])]
    (js/setTimeout hide-error 5000)
    [re-com/alert-box :alert-type :danger
                      :body error-text
                      :closeable? true
                      :heading "Damn..."
                      :on-close hide-error]))

(defn modal
  []
  (when @(subscribe [::subs/modal?])
    [re-com/modal-panel :backdrop-color "#3F5765"
                        :child [re-com/h-box :children [[loading-indicator] [error]]]]))

(defn preamble
  []
  [re-com/title :label config/preamble
                :level :level1])

(defn input
  []
  (let [subject (subscribe [::subs/subject])]
    [re-com/input-text :attr {:auto-focus true}
                       :change-on-blur? false
                       :class "inline-input rc-title level1"
                       :model subject
                       :on-change #(dispatch [::events/change-subject %])
                       :validation-regex #"^.{0,20}$"]))

(defn not-your-man
  []
  (let [data (subscribe [::subs/not-your-man])]
    [:ul#not-your-man.rc-title.level1 (for [item @data] [:li {:key item} item])]))

(defn verdict
  []
  (let [[text actual-subject] @(subscribe [::subs/verdict])
        url @(subscribe [::subs/thing])
        thing (if url
                [:a {:href (or url "#")} actual-subject]
                actual-subject)]
    [re-com/title :label [:p text thing "."]
                  :level :level1]))

(defn another-man!
  []
  (let [subject (subscribe [::subs/subject])]
    [re-com/md-icon-button :md-icon-name "zmdi-refresh"
                           :on-click #(dispatch [::events/sparql-query])
                           :size :larger
                           :tooltip (format "That's not my %s!" @subject)
                           :tooltip-position :left-center]))

(defn tweet-button
  []
  (let [url @(subscribe [::subs/tweet])]
    [re-com/hyperlink-href :class "rc-icon-larger"
                           :href url
                           :label [:i.zmdi.zmdi-twitter]
                           :target "_blank"
                           :tooltip "Tweet this wisdom!"
                           :tooltip-position :right-center]))

(defn buttons
  []
  [re-com/h-box :gap "2em"
                :justify :center
                :children [[another-man!] [tweet-button]]])

(defn footer
  []
  [re-com/h-box :children [[:p "Source data from "
                               [:a {:href "https://www.wikidata.org"} [:i.zmdi.zmdi-wikipedia] "ikidata"]]
                           [:p "Idea from "
                               [:a {:href "https://twitter.com/notyourbot1"} "Not Your Bot"]]
                           [:p "Source code at "
                               [:a.zmdi.zmdi-github {:href "https://github.com/jindrichmynarz/not-your-man"}]]]
                :class "footer"
                :gap "2em"
                :justify :center])

(defn main-panel
  []
  [re-com/h-box :children [[re-com/v-box :children [[re-com/h-box :align :baseline
                                                                  :children [[preamble] [input]]]
                                                    [not-your-man]
                                                    [verdict]
                                                    [re-com/gap :size "2em"]
                                                    [buttons]
                                                    [re-com/gap :size "2"]
                                                    [footer]]
                                         :width "66%"]
                           [modal]]
                :justify :center
                :min-height "100vh"])
