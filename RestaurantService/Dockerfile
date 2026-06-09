# ==========================================
# Stage 1: Build & Publish
# ==========================================
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS builder
WORKDIR /src

COPY *.csproj ./
RUN dotnet restore

COPY . .

RUN dotnet publish -c Release -o /app/publish /p:UseAppHost=false

# ==========================================
# Stage 2: Runtime 
# ==========================================
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app

EXPOSE 8084

COPY --from=builder /app/publish .


ENTRYPOINT ["dotnet", "RestaurantService.dll"]