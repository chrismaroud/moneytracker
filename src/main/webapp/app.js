var app = angular.module('MoneyTracker', ['ui.bootstrap', 'ui.utils.masks', 'chart.js']);

app.directive('percentageDirective', function(){
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, modelCtrl) {

            modelCtrl.$parsers.push(function (inputValue) {

                var transformedInput = inputValue + '%';

                if (transformedInput!=inputValue) {
                    modelCtrl.$setViewValue(transformedInput);
                    modelCtrl.$render();
                }

                return transformedInput;
            });
        }
    };
});

app.controller('OverviewController', function ($scope, $uibModal, $http) {
    $scope.loading = true;
    $scope.error = null;
    $scope.overview = null;
    $scope.currencies = [
        {value: 'EUR',label: '€ Euro'},
        {value: 'USD',label: '$ US Dollar'}
    ];
    $scope.user = null;

    $scope.init = function () {

        $scope.loading = true;
        $scope.initialized = true;
        $http({method: 'GET', url: '/rest/user'})
            .then($scope.finishGetUser, $scope.finishErrorCallback);

    };

    $scope.finishGetUser = function(response){
        $scope.user = response.data;

        if ($scope.user == undefined || $scope.user == '') {
            $scope.activeModal = $uibModal.open({
                templateUrl: 'selectCurrencyContent.html',
                backdrop: false,
                size: 'sm',
                keyboard: false,
                scope: $scope,
                windowTopClass: 'overlay'
            });
            $scope.loading = false;
        } else {
            $http.get('/rest/overview').then($scope.finishGetOverview, $scope.finishErrorCallback);
        }

    };

    $scope.finishGetOverview = function(response){
        $scope.overview = response.data;
        $scope.loading = false;
        if (!$scope.overview.currentPriceDate){
            $scope.warning =
                'We\'re unable to get you any prices :( ' +
                'Please allow up to 12 hours for us to collect prices on your assets and have this screen make sense to you. ' +
                '' +
                'No worries, we\'ll send you a nice email report after collecting today\'s prices.';
        }
        if ($scope.overview.assets.length == 0){
            $scope.activeModal = $uibModal.open({
                templateUrl: 'noAssets.html',
                backdrop: false,
                size: 'sm',
                keyboard: false,
                scope: $scope,
                windowTopClass: 'overlay'
            });
        }
        $scope.overview.allocationChart = {};
        $scope.overview.allocationChart.labels = [];
        $scope.overview.allocationChart.values = [];

        $scope.overview.performanceChart = {};
        $scope.overview.performanceChart.labels = [];
        $scope.overview.performanceChart.values = [];
        $scope.overview.performanceChart.series = [];

        var performanceChartInnerValues = [];

        $scope.overview.assets.forEach(function (asset) {
            $scope.overview.allocationChart.labels.push(asset.name);
            $scope.overview.allocationChart.values.push(asset.price);
            performanceChartInnerValues.push(asset.oneMonthYieldPercentage);
            $scope.overview.performanceChart.labels.push(asset.name);

        });
        $scope.overview.performanceChart.values.push(performanceChartInnerValues);



    };

    $scope.finishErrorCallback = function(response){
        $scope.error = response.statusText;
        $scope.loading = false;
    };

    $scope.clearError = function () {
        alert('clear error');
        $scope.error = null;
    };
    $scope.clearWarning = function () {
        alert('clear warning');
        $scope.warning = null;
    };


    $scope.selectCurrency = function(currency){
        alert('selectCurrency: ' + currency);
        $scope.loading = true;
        alert('close modal?');
        $scope.activeModal.close();
        $http.post('/rest/user', {defaultCurrency: currency}).then(
            $scope.init, $scope.finishErrorCallback);
    };

   if (!$scope.initialized) {
        $scope.init();
    }
});

app.controller('AssetController', function ($scope, $uibModal, $http) {
    $scope.asset = {};
    $scope.error = null;
    $scope.currencies = [
        {value: 'EUR', label: '€ Euro'},
        {value: 'USD', label: '$ US Dollar'}
    ];

    $scope.init = function () {
        $scope.loading = true;
        $scope.initialized = true;
        $http({method: 'GET', url: '/rest/asset'})
            .then($scope.finishGetAssets, $scope.finishErrorCallback);

    };

    $scope.finishGetAssets = function(response){
        $scope.loading = false;
        $scope.assets = response.data;
        console.log('finishGetAssets:: ' + JSON.stringify(response.data));
    };

    $scope.editAsset = function(asset){
        $scope.asset = asset ? asset : {};
        $scope.activeModal = $uibModal.open({
            templateUrl: 'editAsset.html',
            backdrop: false,
            size: 'sm',
            keyboard: false,
            scope: $scope,
            windowTopClass: 'overlay'
        });
    };

    $scope.verifyAsset = function(){
        var show = true;
        if (! $scope.asset) {
            console.log('show button: no asset');
            $scope.asset.name = 'Hello world';
            console.log('  scope.asset: ' + $scope.asset);
            console.log('  scope.asset.name: ' + $scope.asset.name);
            show = false;
        } else if (! $scope.asset.name || $scope.asset.name.length < 1){
            console.log('show button: no assetName');
            show = false;
        } else if (!$scope.asset.assetType){
            console.log('show button: no assetType');
            show = false;
        } else if ( !$scope.asset.currency || $scope.asset.currency.length < 3){
            console.log('show button: no currency');
            show = false;
        } else {
            switch ($scope.asset.assetType) {
                case 'CASH':
                    if (!$scope.asset.amount || $scope.asset.amount.length < 1) {
                        show = false;
                        console.log('show button: no amount');
                    } else if (!$scope.asset.interestPercentage || $scope.asset.interestPercentage.length < 1) {
                        show = false;
                        console.log('show button: no interest percentage');
                    }
                    break;
                case 'OPTION':
                    if (!$scope.asset.strikePrice || $scope.asset.strikePrice.length < 1) {
                        console.log('show button: no strike price');
                        show = false;
                        break;
                    }
                case 'SHARE': // fall-through!
                    if (!$scope.asset.isin || $scope.asset.isin.length < 3) {
                        console.log('show button: no isin');
                        show = false;
                    } else if (!$scope.asset.numberOfShares || $scope.asset.numberOfShares.length < 1) {
                        console.log('show button: no numberOfShares');
                        show = false;
                    } else if (!$scope.asset.taxPercentage || $scope.asset.taxPercentage.length < 1) {
                        console.log('show button: no taxPercentage');
                        show = false;
                    }
            }
        }
        console.log('show button: ' + show);
        return show;
    };

    $scope.saveAsset = function(){
        $scope.activeModal.close();
        $scope.loading = true;
        console.log('Save asset ' + JSON.stringify($scope.asset));
        if ($scope.asset.id) {
            $http.put('/rest/asset', $scope.asset).then($scope.init, $scope.finishErrorCallback);
        } else {
            $http.post('/rest/asset', $scope.asset).then($scope.init, $scope.finishErrorCallback);
        }
    };

    $scope.deleteAsset = function(asset){
        if (confirm("Are you sure you want to delete asset '" + asset.name + "'?")){
            console.log('Deleting asset ' + JSON.stringify(asset));
            $http.delete('/rest/asset/'+asset.id).then($scope.init, $scope.finishErrorCallback);
        }
    };

    $scope.finishErrorCallback = function(response){
        $scope.error = response.statusText;
        $scope.loading = false;
    };
    $scope.clearError = function () {
        alert('clear error');
        $scope.error = null;
    };
    $scope.clearWarning = function () {
        alert('clear warning');
        $scope.warning = null;
    };

    if (! $scope.initialized){
        $scope.init();
    }


});




