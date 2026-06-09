using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace RestaurantService.Models
{
    [Table("catalog_menu")]
    public class CatalogMenu
    {
        [Key]
        [Column("id")]
        public string Id { get; set; } = string.Empty;

        [ForeignKey("Restaurant")]
        [Column("restaurant_id")]
        public string RestaurantId { get; set; } = string.Empty;

        [Column("name")]
        public string Name { get; set; } = string.Empty;

        [Column("price")]
        public decimal Price { get; set; }

        [Column("is_available")]
        public bool IsAvailable { get; set; }
    }
}