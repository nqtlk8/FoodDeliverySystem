using Microsoft.EntityFrameworkCore;
using RestaurantService.Models;

namespace RestaurantService.Drivers
{
    public class DatabaseContext : DbContext
    {
        public DatabaseContext(DbContextOptions<DatabaseContext> options) : base(options) { }

        public DbSet<Restaurant> Restaurants { get; set; }
        public DbSet<CatalogMenu> CatalogMenus { get; set; }
    }
}