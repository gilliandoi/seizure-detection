library("R.matlab")
library("bmp")

#電位数
channels <- 0

#時間帯
timeTotal <- 0

#分割シリーズ
series <- 400

#分割制限(<399)
splitLimit <- 0

#出力画像幅
outWidth <- 183

#出力画像高
outHeight <- 183

#入力フォルダパス（mat）
inputDir <- '/R/data'

#出力フォルダパス(jpg)
outputDir <- '/R/output'

#フォルダで存在するmatファイルを分割して出力
readMatFolder <- function(){
	setwd(inputDir)

	matfile <- dir()
	cnt <- grep("mat",matfile)
	
	#cat("-----------Split Data----------------------------------------\n")
	for ( i in 1:length(cnt)){
		path = matfile[cnt[i]];	
		print(path)
		readMatFile(path)		
	}

	cat(paste("---------------------------------------------------------------\n"))
	if(splitLimit != 0){
		cat(paste("Exported",splitLimit*length(cnt),"files!\n"))
	}
	setwd('/R')
	#source('/R/test01.R')
}

#matLabファイル分割して出力
readMatFile <- function(path){
	setwd(inputDir)
	
	#x <- readMat("/R/data/Patient_8_ictal_segment_1.mat")
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#matファイル構成出力
	#print(x[[1]])

	# data 行列
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	#データ分割
	fileAccount=timeTotal/series-1

	i <- 0
	#分割制限数量までのファイルを出力する
	if(splitLimit != 0){
		fileAccount=splitLimit
	}
	while(i<fileAccount){		

		#各ファイル出力
		cat(paste("File",i+1," OK!\n"))

		#グラフ出力用行列作成
		outputData <- list()

		j <- 1
		while(j<=channels){
			#各電位座標算出			
			#時間帯分割
			timeFrom <- i*series+1
			timeTo <- (i+1)*series
			#cat(paste("rowIndex:",j,",range:",timeFrom,":",timeTo,"\n"))
			electrode <- data[j,timeFrom :timeTo ]
			#オリジナルデータ（mat）
			#str(electrode)
			#ベクトル出力するため、y値を変更
			electrode <- electrode*0.0214+(channels-j)*34

			#作成したベクトルを x の末尾に追加する
			outputData <- c(outputData,list(electrode))	

			#グラフ出力
			if (j==1){
				#lim:座標範囲、lab：座標ラベル、axes：座標軸非表示
				par(mar=c(0,0,0,0))
				plot(outputData[[1]],type="l",ylim=c(0,516),xlab="",ylab="",axes=FALSE)
			}else{
				#同じ画像で編集
				par(new=T)
				#lim:座標範囲、lab：座標ラベル、axes：座標軸非表示
				plot(outputData[[j]],type="l",ylim=c(0,516),xlab="",ylab="",axes=FALSE)
			}			
		
			j <- j + 1
		}


		#ファイル出力
		setwd(outputDir)
		
		#出力ファイル名
		outputFile <- paste(substr(path, 1, nchar(path) - 4),"_",formatC(i+1,width=3,flag="0"),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outWidth)
		dev.off()
	
		#座標出力
		#cat("-----------Output Data-------------\n")
		#str(outputData)
		
		i <- i + 1
	}	
}

#TEST用：BMPファイル読込む
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()