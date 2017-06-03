(function () {
    var app = angular.module('app', [
        'ngWebSocket', 'ngRoute', 'ngResource', 'ui.bootstrap', 'cgPrompt'
    ])
    app.factory('MyData', function ($websocket) {
        var dataStream = $websocket('ws://webtask.**************:8068/ws/currencies');

        var collection = [];
        var error = false;
        
        dataStream.onMessage(function (message) {
            collection[0] = JSON.parse(message.data);
            collection[0].PublicationDate = new Date(collection[0].PublicationDate).toLocaleString('pl-PL');
            error = false;
        });

        dataStream.onError(function (message) {
            error = true;
        });

        var methods = {
            collection: collection,
            error: error,
            get: function () {
                dataStream.send(JSON.stringify({action: 'get'}));
            }
        };

        return methods;
    })
    app.controller('ExchangeController', function ($scope, $http, prompt, MyData) {
        var self = this;
        self.MyData = MyData;
        $http.get('/user/').then(function (response) {
            self.user = response.data;
        });
        window.setInterval(function() {
            $http.get('/user/').then(function (response) {
                self.user = response.data;
            })}, 10000
        );

        $scope.goBuy = function(curr) {
            prompt({
                title: 'Buy ' + curr,
                message: 'Enter amount',
                input: true,
                label: '',
                value: ''
            }).then(function(number){
                var data = {
                    code: curr,
                    amount: number
                };
                $http.post('/buy/', data).then(function(response) {
                    self.user = response.data;
                });
            });
        };

        $scope.goSell = function(curr) {
            prompt({
                title: 'Sell ' + curr,
                message: 'Enter amount',
                input: true,
                label: '',
                value: ''
            }).then(function(number){
                var data = {
                    code: curr,
                    amount: number
                };
                $http.post('/sell/', data).then(function(response) {
                    self.user = response.data;
                });
            });
        };
    });
})();