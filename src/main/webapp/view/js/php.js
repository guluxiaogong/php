var internal = {
    flag: true,
    init: function () {
        //internal.background();
        internal.search();
        internal.doLayout();
        window.onresize = function () {
            internal.doLayout();
        };
        //console.log($(window).height());


    },
    doLayout: function () {
        var personBoxH = $(window).height() - $('#header').outerHeight() - $('.page-header').outerHeight() - 15;
        $('.person-box').height(personBoxH);
        $('.person-box').css('background-size', personBoxH * 0.3 + 'px ' + personBoxH * 0.9 + 'px');

    },
    search: function () {
        var imag = "img/man.png";
        $('.person-box').css('background', 'url(img/man.png) no-repeat center');
        $('#persionSearch').on('click', function () {

            if (internal.flag) {
                imag = 'url(img/wman.png) no-repeat center';
                internal.flag = false;
            } else {
                imag = 'url(img/man.png) no-repeat center';
                internal.flag = true;
            }
            var personBoxH = $(window).height() - $('#header').outerHeight() - $('.page-header').outerHeight() - 15;
            $('.person-box').css({
                'background': imag,
                'backgroundSize': personBoxH * 0.3 + 'px ' + personBoxH * 0.9 + 'px'
            });
        });
    },
    background: function () {
        $('.person-box').css('background', 'url(img/man.png) no-repeat center');
    }

};