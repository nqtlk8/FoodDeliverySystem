using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using RestaurantService.Drivers;
using RestaurantService.Models;
using System.Text.Json;

namespace RestaurantService.Controllers
{
    [ApiController]
    [Route("api/restaurant")]
    public class RestaurantController : ControllerBase
    {
        private readonly DatabaseContext _dbContext;
        private readonly KafkaProducer _kafkaProducer;

        public RestaurantController(DatabaseContext dbContext, KafkaProducer kafkaProducer)
        {
            _dbContext = dbContext;
            _kafkaProducer = kafkaProducer;
        }

        // API: Đăng ký một nhà hàng mới vào hệ thống
        [HttpPost("register")]
        public async Task<IActionResult> RegisterRestaurant([FromBody] Restaurant restaurant)
        {
            if (string.IsNullOrEmpty(restaurant.Id)) 
                restaurant.Id = Guid.NewGuid().ToString();
            
            _dbContext.Restaurants.Add(restaurant);
            await _dbContext.SaveChangesAsync();
            
            return Ok(new { message = "Đăng ký nhà hàng thành công!", data = restaurant });
        }

        // API: Thêm một món ăn thật vào thực đơn của nhà hàng
        [HttpPost("menu/add")]
        public async Task<IActionResult> AddDish([FromBody] CatalogMenu dish)
        {
            // Kiểm tra ràng buộc Foreign Key để chắc chắn nhà hàng phải tồn tại trước khi thêm món
            var restaurantExists = await _dbContext.Restaurants.AnyAsync(r => r.Id == dish.RestaurantId);
            if (!restaurantExists) 
                return BadRequest(new { message = "Nhà hàng này không tồn tại trong hệ thống!" });

            if (string.IsNullOrEmpty(dish.Id)) 
                dish.Id = Guid.NewGuid().ToString();

            _dbContext.CatalogMenus.Add(dish);
            await _dbContext.SaveChangesAsync();

            return Ok(new { message = "Thêm món ăn vào Menu thành công!", data = dish });
        }

        // API phục vụ UI: Lấy danh sách món ăn đang khả dụng
        [HttpGet("dishes")]
        public async Task<IActionResult> GetAvailableDishes()
        {
            var dishes = await _dbContext.CatalogMenus
                                         .Where(d => d.IsAvailable)
                                         .AsNoTracking()
                                         .ToListAsync();
            return Ok(dishes);
        }

        // FR-RES-01 & 02: Cập nhật tình trạng tồn kho/Hết hàng qua Web Dashboard
        [HttpPut("menu/{id}/availability")]
        public async Task<IActionResult> UpdateAvailability(string id, [FromBody] bool isAvailable)
        {
            var menuItem = await _dbContext.CatalogMenus.FindAsync(id);
            if (menuItem == null) return NotFound(new { message = "Không tìm thấy món ăn" });

            menuItem.IsAvailable = isAvailable;
            await _dbContext.SaveChangesAsync();

            return Ok(new { message = "Cập nhật trạng thái món ăn thành công!", data = menuItem });
        }

        // FR-RES-03: Thay đổi giá món ăn và Phát sự kiện Price_Changed lên Kafka
        [HttpPut("menu/{id}/price")]
        public async Task<IActionResult> UpdatePrice(string id, [FromBody] decimal newPrice)
        {
            var traceId = Request.Headers["X-Trace-Id"].ToString() ?? Guid.NewGuid().ToString();
            var userId = Request.Headers["X-User-Id"].ToString() ?? "UNKNOWN_OWNER";

            var menuItem = await _dbContext.CatalogMenus.FindAsync(id);
            if (menuItem == null) return NotFound(new { message = "Không tìm thấy món ăn" });

            menuItem.Price = newPrice;
            await _dbContext.SaveChangesAsync();

            var eventPayload = new
            {
                item_id = menuItem.Id,
                restaurant_id = menuItem.RestaurantId,
                new_price = menuItem.Price,
                updated_at = DateTime.UtcNow
            };

            string jsonPayload = JsonSerializer.Serialize(eventPayload);
            
            await _kafkaProducer.PublishAsync("restaurant.price-changed", menuItem.Id, jsonPayload, traceId, userId);

            return Ok(new { message = "Thay đổi giá thành công và đã đồng bộ sự kiện!", data = menuItem });
        }
    }
}