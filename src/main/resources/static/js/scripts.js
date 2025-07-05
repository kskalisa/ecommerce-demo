document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.add-cart').forEach(function(btn) {
        btn.addEventListener('click', function() {
            alert('Added to cart!');
        });
    });
    var buyNow = document.querySelector('.btn-primary');
    if (buyNow) {
        buyNow.addEventListener('click', function() {
            alert('Proceed to buy!');
        });
    }
});