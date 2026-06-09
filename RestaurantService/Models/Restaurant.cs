using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace RestaurantService.Models
{
    [Table("restaurants")]
    public class Restaurant
    {
        [Key]
        [Column("id")]
        public string Id { get; set; } = string.Empty;

        [Column("owner_id")]
        public string OwnerId { get; set; } = string.Empty;

        [Column("name")]
        public string Name { get; set; } = string.Empty;
    }
}