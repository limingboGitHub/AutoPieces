//
// Created by Lenovo on 2021/3/26.
//
#include "com_example_autopieces_cpp_MoveMethod.h"
#include "math.h"
#include "list"
using namespace std;

struct Node{
    int x,y;
    int f;
    int g;
    int h;
    struct Node* parent;
};

Node* getNearestNode(list<Node*> nodeList)
{
    Node* minNode = NULL;
    int tempF = 0;
    for (list<Node*>::iterator it = nodeList.begin(); it!=nodeList.end(); it++) {
        if(tempF == 0 || (*it)->f<tempF){
            minNode = (*it);
            tempF = (*it)->f;
        }
    }
    return minNode;
}

void removeFromList(Node* node,list<Node*>& nodeList)
{
    for (list<Node*>::iterator it = nodeList.begin(); it!=nodeList.end(); it++) {
        if (node->x==(*it)->x && node->y == (*it)->y){
            nodeList.erase(it);
            return;
        }
    }
}

bool containNode(Node* node,list<Node*> nodeList)
{
    for (list<Node*>::iterator it = nodeList.begin(); it!=nodeList.end(); it++) {
        if (node->x==(*it)->x && node->y == (*it)->y)
            return true;
    }
    return false;
}

int distance(int x1,int y1,int x2,int y2)
{
    return (int)sqrt((x1-x2)*(x1-x2)*100 + (y1-y2)*(y1-y2)*100);
}

extern "C" JNIEXPORT jintArray JNICALL Java_com_example_autopieces_cpp_MoveMethod_calculateMovePath
  (JNIEnv *env, jclass jobect,
  jint startX, jint startY,
  jint endX, jint endY,
  jintArray map, jint row, jint col)
{
    jint* mapInt = env->GetIntArrayElements(map,JNI_FALSE);
    //起点
    Node* startNode = new Node;
    startNode->x = startX;
    startNode->y = startY;
    startNode->g = 0;
    startNode->h = distance(startX,startY,endX,endY);
    startNode->parent = nullptr;
    //终点
    Node* endNode = new Node;
    endNode->x = endX;
    endNode->y = endY;

    list<Node*> startList;
    list<Node*> endList;

    startList.push_back(startNode);

    list<pair<int,int>> offsetList;
    offsetList.emplace_back(0,1);
    offsetList.emplace_back(0,-1);
    offsetList.emplace_back(1,0);
    offsetList.emplace_back(-1,0);

    Node* nowNode = nullptr;
    while(!containNode(endNode,startList)){
        //获取startList列表中f值最小的点
        nowNode = getNearestNode(startList);
        if(nowNode == nullptr){
            break;
        }

        //获取nowNode的周围节点
        list<Node*> nearList;
        for(auto & it : offsetList)
        {
            int i = it.first;
            int j = it.second;

            //越界点跳过
            int tempX = nowNode->x+i;
            int tempY = nowNode->y+j;
            if (tempX<0 || tempX>col|| tempY<0 || tempY>row)
                continue;
            //是否有障碍
            if (mapInt[tempY*col + tempX]!=0){
                if (tempX!=endNode->x || tempY!=endNode->y)
                    continue;
            }
            //startList,endList表中存在，则跳过
            Node node{};
            node.x = tempX;
            node.y = tempY;
            if(containNode(&node,startList)){
                continue;
            }
            if(containNode(&node,endList))
                continue;
            Node* nearNode = new Node;
            nearNode->x = tempX;
            nearNode->y = tempY;
            nearNode->g = nowNode->g + distance(nowNode->x,nowNode->y,tempX,tempY);
            nearNode->h = distance(tempX,tempY,endNode->x,endNode->y);
            nearNode->f = nearNode->g + nearNode->h;
            nearNode->parent = nowNode;
            nearList.push_back(nearNode);
        }
        //将当前节点放入endList
        endList.push_back(nowNode);
        removeFromList(nowNode,startList);
        //将周围点放入startList
        for(auto & it : nearList){
            startList.push_back(it);
        }
    }
    if(nowNode == nullptr){
        startList.clear();
        endList.clear();
        return nullptr;
    }

    Node* findNode = nullptr;
    for(auto & it : startList)
    {
        if (it->x == endNode->x &&
        it->y == endNode->y)
        {
            findNode = it;
            break;
        }
    }

    if (findNode == nullptr){
        startList.clear();
        endList.clear();
        return nullptr;
    }

    list<pair<int,int>> pathList;
    findNode = findNode->parent;
    while(findNode){
        pathList.emplace_back(findNode->x,findNode->y);
        findNode = findNode->parent;
    }
    pathList.pop_back();

    int length = pathList.size()*2;
    int pathInt[length];
    int index = 0;
    for(auto & it : pathList)
    {
        pathInt[index++] = it.first;
        pathInt[index++] = it.second;
    }
    jintArray movePath = (*env).NewIntArray(pathList.size()*2);
    env->SetIntArrayRegion(movePath,0,length,pathInt);
    return movePath;
}

