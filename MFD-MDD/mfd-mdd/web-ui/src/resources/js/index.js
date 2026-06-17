var tabLinks = document.querySelectorAll('.tabs ul li a');
for (var i = 0; i < tabLinks.length; i++) {
    tabLinks[i].onclick = function() {
        var target = this.getAttribute('href').replace('#', '');
        var sections = document.querySelectorAll('section.tab-content');

        for(var j=0; j < sections.length; j++) {
            sections[j].style.display = 'none';
        }

        document.getElementById(target).style.display = 'block';

        for(var k=0; k < tabLinks.length; k++) {
            tabLinks[k].removeAttribute('class');
        }

        this.setAttribute('class', 'active');

        return false;
    }
}