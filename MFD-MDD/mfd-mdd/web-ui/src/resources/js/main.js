import $ from 'jquery';

export function dinamicInitialization() {
    let mainJs = {
        show: function () {
            $(".multiSelect").click(function () {
                $(this).siblings(".multiSelect-list").toggleClass("show");
            });
        },

        err: function () {
            if ($(".registration__form-item-input").hasClass('registration__form-item-input-err')) {
                $('.registration__form-item-input-err').append("<span data-style>Appended text<br>xcvxcvxcv xcv xc vxc vxcv xcv xcv xcv xc<br>xdcvxcvxc vxcv xc xcv</span>");

            }
        },

        modal: function () {
            $(".modal__window").click(function () {
                $(".modal__wrapper-overlay").addClass("modal__wrapper-overlay-show");
                $(".mask").addClass("mask__show");
            });

            $(".mask").click(function () {
                $(".modal__wrapper-overlay").removeClass("modal__wrapper-overlay-show");
                $(".mask").removeClass("mask__show");
            });


            $(".modal__close-btn").click(function () {
                $(".modal__wrapper-overlay").removeClass("modal__wrapper-overlay-show");
                $(".mask").removeClass("mask__show");
            });

        },

        Inputcheck: function () {
            $(".check__line").click(function () {
                $(this).toggleClass("check__line-true");
            })

            $(".check__line").click(function () {
                $(this).prev(".check__point").toggleClass("check__point-true");
            })
        },


        timeEdit: function () {
            $(".board__input-time").click(function () {
                $(this).next(".time__edit").toggleClass("time__edit-show");
            })

            $(".time__edit-btn").click(function () {
                $(this).parent(".time__edit").removeClass("time__edit-show");
            })
        },

        showPass: function () {
            $(".eye-show").click(function () {
                $(this).toggleClass("eye-hide");
            })
        },




        burger: function () {

            //$(".main__bar-item-hide").slideUp();
            $(".main__bar-item-btn").click(function () {

                if ($('input').attr('disabled')) {
                    $(this).parent('div').addClass('disabled__input');
                    $(this).addClass('asdasdasdas');
                }

                var hasOpen = $(this).hasClass('main__bar-item-list-open');
                $(".main__bar-item-btn").each(function (index, el) {
                    var obj = $(el);
                    if (obj.hasClass('main__bar-item-list-open')) {
                        obj.toggleClass("main__bar-item-list-open");
                        obj.next(".main__bar-item-hide").slideToggle();
                    }
                });
                if (!hasOpen) {
                    $(this).toggleClass("main__bar-item-list-open");
                    $(this).next(".main__bar-item-hide").slideToggle();
                }

            })

            $(".main__bar-item-hide-item").click(function () {
                $(this).parent(".main__bar-item-hide").slideDown();
            })


            $(".menu").click(function () {
                $(".main__bar").animate({ width: 'toggle' });
            });

        },


        alert: function () {
                $('body').on('mouseover', '.row__title', function (){
                    $(this).clone().appendTo(this);
            })
        },

        alert1: function () {
            $('body').on('mouseout', '.row__title', function (){
                $(this).children('.row__title').remove();
            })
        },


        disabledInput: function () {
                alert('asdasd');
                if ($('input').hasAttribute('disabled')) {
                    $(this).parent('div').addClass('disabled__input');
                    $(this).addClass('asdasdasdas');
                }
        },

        iconBurger: function () {
            var Menu = {

                el: {
                    ham: $('.menu'),
                    menuTop: $('.menu-top'),
                    menuMiddle: $('.menu-middle'),
                    menuBottom: $('.menu-bottom')
                },

                init: function () {
                    Menu.bindUIactions();
                },

                bindUIactions: function () {
                    Menu.el.ham
                        .on(
                        'click',
                        function (event) {
                            Menu.activateMenu(event);
                            event.preventDefault();
                        }
                        );
                },

                activateMenu: function () {
                    Menu.el.menuTop.toggleClass('menu-top-click');
                    Menu.el.menuMiddle.toggleClass('menu-middle-click');
                    Menu.el.menuBottom.toggleClass('menu-bottom-click');
                    Menu.el.ham.toggleClass('menu-hide-padding');
                }
            };

            Menu.init();

        },

        filterBurger: function () {

        $("body").on("click", ".dropdown__text", function () {


            $(this).siblings(".dropdown__list").slideDown();
            $(this).siblings(".dropdown__list-hide").slideDown();

            $(this).parents(".filter__dropdown").siblings(".filter__dropdown").children(".dropdown__list").slideUp();
            $(this).parents(".filter__dropdown").siblings(".filter__dropdown").children(".dropdown__list-hide").slideUp();

            $(this).siblings(".dropdown__list").removeClass("hide");
            $(this).siblings(".dropdown__list-hide").removeClass("hide");

            $(".hide__btn").click(function () {
                $(".dropdown__list-hide").slideUp();
                $(".dropdown__list").slideUp();
            })
        })
    }//,

        // fullHeader: function () {
        //     $('body').on('click', '.row__btn', function (){
        //         $('.row__btn').removeClass("pressed");
        //         $(this).addClass("pressed");
        //     })
        //
        //     $('body').on('click', '.pressed', function (){
        //         $('.row__btn').removeClass("pressed");
        //     })
        // }

    };

    mainJs.err();
    mainJs.modal();
    mainJs.Inputcheck();
    mainJs.timeEdit();
    mainJs.showPass();
    mainJs.burger();
    mainJs.iconBurger();
    mainJs.filterBurger();
}

export function modalInitialization() {
    let mainJs = {

        showAlert: function () {
            $(".board__pagination-page").click(function () {
                $(this).toggleClass("");
                /* $(".row__alert-list").toggleClass("show");*/
            })
        },

        modalBurger: function () {
            $('.profile__main-slide').click(function () {;
                $('.profile__main-slide').removeClass("active");
                $(this).addClass("active");
                $('.profile__main-slide').removeClass('open');
                $(".profile__main-slide-content").removeClass('show');
                $(this).siblings(".profile__main-slide-content").toggleClass('show');
            });

        }
}



    mainJs.modalBurger();
    mainJs.showAlert();
}