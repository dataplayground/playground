var myApp = angular.module('myApp', ['ngWebSocket']);

myApp.controller('MainCtrl', function ($scope) {

    $scope.list = [];
    $scope.text = 'spark';


    $scope.filterForm = {};
    $scope.filterForm.keyword = "spark";

    $scope.submit = function () {
        if ($scope.filterForm.keyword) {
            $scope.list.push(this.filterForm.keyword);
            $scope.filterForm.keyword = '';
        }
    };


});

myApp.factory('MyData', function ($websocket) {
        // Open a WebSocket connection
        var dataStream = $websocket('wss://localhost:9000/socket');
        //This works
        //var dataStream = $websocket('wss://echo.websocket.org/');

        var collection = [];

        dataStream.onMessage(function (message) {
            collection.push(JSON.parse(message.data));
        });

        var methods = {
            collection: collection,
            get: function () {
                dataStream.send(JSON.stringify({foo: 'bar'}));
            }
        };

        return methods;
    })
    .controller('SomeController', function ($scope, MyData) {
        $scope.MyData = MyData;
        $scope.testData = "asdf"

        MyData.get();
    });
