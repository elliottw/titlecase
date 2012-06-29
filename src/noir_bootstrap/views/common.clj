(ns noir-bootstrap.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "Titlecase.org"]
               (include-css "/css/bootstrap.css")
               (include-css "/css/bootstrap-responsive.css")
               [:style "body { padding-top: 60px; }"]
               [:meta {:name "viewpoint" :content "width=device-width" :initial-scale "1" :maximum-scale "1"}]]
              [:body
               (list
                [:div.navbar.navbar-fixed-top {"data-toggle" "collapse" "data-target" ".nav-collapse"}
                 [:div.navbar-inner
                  [:div.container
                   [:a.btn.btn-navbar
                    [:span.icon-bar]]
                  [:a.brand {"href" "/"} "Titlecase"]
                   [:div.nav-collapse
                    [:ul.nav
                     [:li
                      [:a {"href" "comparison"} "Comparison"]]
                     [:li
                      [:a {"href" "assumptions"} "Assumptions"]]
                     [:li
                      [:a {"href" "https://github.com/elliottw/titlecase"} "Source"]]]]]]]
                [:div.container content] 
                (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
                (include-js "/js/bootstrap.min.js"))]))
