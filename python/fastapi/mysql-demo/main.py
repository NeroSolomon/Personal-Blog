# https://blog.csdn.net/weixin_45248616/article/details/124184258
from fastapi import FastAPI

app = FastAPI()

import pymysql
pymysql.install_as_MySQLdb()

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

# 配置数据库地址：数据库类型+数据库驱动名称://用户名:密码@机器地址:端口号/数据库名
engine = create_engine("mysql+pymysql://root:Hello1024@127.0.0.1:3306/trip_django")

# 把当前的引擎绑定给这个会话；
Session = sessionmaker(autocommit=False, autoflush=False, bind=engine)
# 实例化
session = Session()

# declarative_base类维持了一个从类到表的关系，通常一个应用使用一个Base实例，所有实体类都应该继承此类对象
from sqlalchemy.ext.declarative import declarative_base
# 实例化
Base = declarative_base()

from sqlalchemy import Column, String, Integer

# 创建数据库
class User(Base):
  # 表名
  __tablename__ = 'tbuser'
  # 字段
  # primary_key=True 设为主键
  userid = Column(Integer, primary_key=True)
  username = Column(String(255))

  # 构造函数
  def __init__(self, useid, username):
    self.userid = useid
    self.username = username
  
  # 打印
  def __str__(self):
    return "id：%s, name：%s" % (str(self.userid) ,self.username)

  # 定义返回结果
  def to_dict(self):
    return {
      "userid": self.userid,
      "username": self.username
    }

# 在数据库中生成表
Base.metadata.create_all(bind=engine)

from pydantic import BaseModel
# 定义数据模型
class CreateUser(BaseModel):
  userid: int
  username: str

# 添加单个
@app.post("/user/addUser")
async def InsertUser(user: CreateUser):
  try:
    dataUser = User(useid=user.userid, username=user.username)
    session.add(dataUser)
    session.commit()
    session.close()
  except ArithmeticError:
    return {"code":"0002","message":"数据库异常"}
  return {"code":"0000","message":"添加成功"}

# 哪找user_id查询
@app.get("/user/{user_id}")
async def queryUserByUserId(user_id: int):
  try:
    user = session.query(User).filter(User.userid==user_id).first()
    session.close()
    if user:
      return {"code":"0000","message":"请求成功","data":user}
    else:
      return {"code":"0001","message":"查询无结果"}
  except ArithmeticError:
      return {"code":"0002","message":"数据库异常"}

@app.delete("/user/deleteUser/{user_id}")
async def deleteUser(user_id: int):
    try:
        user1 = session.query(User).filter(User.userid == user_id).first()
        if user1:
          session.delete(user1)
          session.commit()
          session.close()
          return {"code": "0000", "message": "删除成功"}
        else:
          return {"code": "0001", "message": "参数错误"}
    except ArithmeticError:
        return {"code": "0002", "message": "数据库错误"}

if __name__ == '__main__':
    import uvicorn
    uvicorn.run(app=app, host="127.0.0.1", port=8000, debug=True)