Feature: MongoDB 連線驗證
  Scenario: 成功連線至 MongoDB
    Given MongoDB container 已啟動
    When 執行 ping 命令
    Then 回傳成功狀態
