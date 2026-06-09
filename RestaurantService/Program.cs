using Microsoft.EntityFrameworkCore;
using RestaurantService.BackgroundServices;
using RestaurantService.Drivers;
using Confluent.Kafka;
using Confluent.Kafka.Admin;

var builder = WebApplication.CreateBuilder(args);

// 1. Đăng ký Database Context (PostgreSQL)
builder.Services.AddDbContext<DatabaseContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("PostgreSQL")));

// 2. Đăng ký hạ tầng Kafka điều hướng
builder.Services.AddSingleton<KafkaProducer>();
builder.Services.AddHostedService<KafkaConsumerService>();

builder.Services.AddControllers();

var app = builder.Build();

// Tự động khởi tạo cấu trúc Database và Kafka Topic khi ứng dụng khởi động
using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    
    // I. Khởi tạo cấu trúc Database thật (Không nạp dữ liệu mồi)
    var dbContext = services.GetRequiredService<DatabaseContext>();
    dbContext.Database.EnsureCreated();

    // II. Tự động kiểm tra và tạo Kafka Topic nếu chưa tồn tại
    var bootstrapServers = builder.Configuration.GetValue<string>("Kafka:BootstrapServers") ?? "kafka:9092";
    var adminConfig = new AdminClientConfig { BootstrapServers = bootstrapServers };
    
    using (var adminClient = new AdminClientBuilder(adminConfig).Build())
    {
        try
        {
            adminClient.CreateTopicsAsync(new TopicSpecification[] {
                new TopicSpecification { Name = "driver.assigned", NumPartitions = 1, ReplicationFactor = 1 }
            }).GetAwaiter().GetResult();
            
            Console.WriteLine("[Kafka Admin] Đã xác nhận/tạo thành công topic: driver.assigned");
        }
        catch (CreateTopicsException e)
        {
            if (e.Results[0].Error.Code == ErrorCode.TopicAlreadyExists)
            {
                Console.WriteLine("[Kafka Admin] Topic 'driver.assigned' đã tồn tại, sẵn sàng sử dụng.");
            }
            else
            {
                Console.WriteLine($"[Kafka Admin Error]: Không thể tự động tạo topic: {e.Results[0].Error.Reason}");
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"[Kafka Admin Error]: Lỗi kết nối đến Broker khi kiểm tra topic: {ex.Message}");
        }
    }
}

app.UseAuthorization();
app.MapControllers();

app.Run();