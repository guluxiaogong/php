var internal = {
    flag: true,
    tagNum: 9,
    qualifiers: [],
    init: function () {
        //初始化加载数据
        //internal.loadData();

        //搜索加载数据
        internal.search();
        internal.doLayout();
        window.onresize = function () {
            internal.doLayout();
            //标签位置随窗口大小自缩放
            if (internal.qualifiers.length > 0)
                internal.buildTag(internal.qualifiers);
        };
        //internal.buildTag(internal.tagNum);
        //临时测试用
        internal.buildDropdown();
    },
    doLayout: function () {
        var $personBox = $('.person-box'),
        //personBoxH = $(window).height() - $('.navbar .container').outerHeight() - $('.row').outerHeight() - 51;
            personBoxH = $(window).height() - $('.row').outerHeight() - 51;
        $personBox.height(personBoxH);
        $personBox.css('background-size', personBoxH * 0.2 + 'px ' + personBoxH * 0.8 + 'px');
        $('.loading').css({
            left: ($(window).width() / 2 - 100 ) + 'px'
        });

    },
    search: function () {
        // var imag = 'url(image/man.png) no-repeat center', $personBox = $('.person-box');
        // $personBox.css('background', imag);

        //搜索按钮加载
        $('#persionSearch').on('click', function () {
            internal.loadData();
        });
        //enter 键加载
        $(document).keydown(function (event) {
            //console.log(event.keyCode);
            if (event && event.keyCode == 13) { // enter 键
                internal.loadData();
            }
        });
    },
    loadData: function () {
        $.ajax({
            url: 'hbase/filterByName',
            data: {
                value: $.trim($('#inputText').val())
            },
            beforeSend: function (XMLHttpRequest) {
                internal.clearTag();
                $('.loading').show();
            },
            success: function (result) {
                if (result && result.length) {


                    var qualifiers = result[0].row;
                    internal.qualifiers = qualifiers;

                    $.each(qualifiers, function (index, item) {
                        if (item.qualifier == 'sex') {
                            var imag = '';
                            if (item.value == 'female') {
                                imag = 'url(image/wman.png) no-repeat center';
                                internal.flag = false;
                            } else {
                                imag = 'url(image/man.png) no-repeat center';
                                internal.flag = true;
                            }
                            //var personBoxH = $(window).height() - $('.navbar .container').outerHeight() - $('.row').outerHeight() - 51;
                            var personBoxH = $(window).height() - $('.row').outerHeight() - 51;

                            $('.person-box').css({
                                'background': imag,
                                'backgroundSize': personBoxH * 0.2 + 'px ' + personBoxH * 0.8 + 'px'
                            });
                        }

                    });

                    //var imag = '';
                    //if (internal.flag) {
                    //    imag = 'url(image/wman.png) no-repeat center';
                    //    internal.flag = false;
                    //} else {
                    //    imag = 'url(image/man.png) no-repeat center';
                    //    internal.flag = true;
                    //}
                    //var personBoxH = $(window).height() - $('.navbar .container').outerHeight() - $('.row').outerHeight() - 51;
                    //$('.person-box').css({
                    //    'background': imag,
                    //    'backgroundSize': personBoxH * 0.2 + 'px ' + personBoxH * 0.8 + 'px'
                    //});


                    internal.buildTag(qualifiers);
                } else {
                    internal.clearTag();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                $('.loading').hide();
            },
            complete: function (XMLHttpRequest, textStatus) {
                $('.loading').hide();
            }
        });
    },
    clearTag: function () {
        var $personBox = $('.person-box');
        if ($personBox.children().length) {
            $personBox.children().remove();
            $personBox.css({
                'background': ''
            });
        }
    },
    buildTag: function (qualifiers) {//标签集合
        var $personBox = $('.person-box'),
            personBoxH = $personBox.height(),
            left_x = $(window).width() / 2,
            short_x = personBoxH * 0.2,
            long_y = personBoxH * 0.4,
            count = qualifiers.length - 2,
            step = 360 / (count | 1),//60//参数表示几等分
            angle = 30;//初始角度
        var personTag = $('.person-tag');
        for (var i = 0; i < count; i++) {
            if (qualifiers[i].qualifier != 'name' && qualifiers[i].qualifier != 'sex') {
                if (angle >= 180)
                    angle = angle - 360;

                var position = internal.getCoordinate(short_x, long_y, angle);//(短半轴，长半轴)//0 60 120 180 240 300

                if (!personTag.length) {//新建
                    var $tag = $("<div class='person-tag' style='width:110px;height: 38px; background-color: #0be386;border-radius:10px;position: absolute;left:" + (position.x + left_x - short_x - 55) + "px;top:" + (position.y + 80) + "px;'><label style='display:block;font-size:24px; text-align:center;color:white;font-family:宋体'>" + qualifiers[i].value + "</label></div>");
                    $tag.appendTo('.person-box');
                } else {//频幕缩放
                    $(personTag[i]).css({
                        top: position.y + 80,
                        //top: position.y + 140,
                        left: position.x + left_x - short_x - 55
                    });
                }

                angle = angle + step;//角度增长
            }

        }

    },
    // 根据角度 获取椭圆上一点的坐标
    // x2⒈/a2+y2/b2=1
    // y = x * tanα
    // a长半轴
    // b短半轴
    // rotate角度
    getCoordinate: function (a, b, rotate) {
        var x = 0, y = 0, tan = 0, rad = 0;

        if (Math.abs(rotate) > 90)
            rad = (Math.abs(rotate) - 90);
        else
            rad = (90 - Math.abs(rotate));
        rad = rad * 2 * Math.PI / 360;
        tan = Math.tan(rad);
        x = Math.sqrt(1 / (1 / (a * a) + (tan * tan) / (b * b)));
        y = x * tan;

        if (rotate < 0)
            x = 0 - x;
        if (rotate > -90 && rotate < 90)
            y = 0 - y;
        x = a + x;
        y = b + y;

        return {
            x: Math.round(x),
            y: Math.round(y)
        };
    },
    buildDropdown: function () {
        //var name = ['张三', '李四', '王五', '赵六','洪七'];
        var name = ['zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'hongqi'];
        $.each(name, function (index, item) {
            var el = $("<li><a href='#'>" + item + "</a></li>");
            el.on('click', function () {
                //alert(el.find('a').text());
                $('#inputText').val(el.find('a').text());
                internal.loadData();
            });
            el.appendTo('#dropdownLi')
        });


    }

};