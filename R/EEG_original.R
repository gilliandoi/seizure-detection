library("R.matlab")
library("bmp")

#�d�ʐ�
channels <- 0

#���ԑ�
timeTotal <- 0

#�����V���[�Y
series <- 400

#��������(<399)
splitLimit <- 0

#�o�͉摜��
outWidth <- 183

#�o�͉摜��
outHeight <- 183

#���̓t�H���_�p�X�imat�j
#inputDir <- '/R/data'
inputDir <- '/R/data'

#�o�̓t�H���_�p�X(jpg)
outputDir <- '/R/output'

#�t�H���_�ő��݂���mat�t�@�C���𕪊����ďo��
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

#matLab�t�@�C���������ďo��
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#mat�t�@�C���\���o��
	#print(x[[1]])

	# data �s��
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	setwd(outputDir)

		#�O���t�o�͗p�s��쐬
		outputData <- list()
		
		#�����g�`�iX�F���ԁAY�F�U���j�F
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(0, 0, 0, 0)) 
		plot(data[1,],type="l",xlab="",ylab="",axes=FALSE,col=1)
		par(new=T)
		plot(data[2,],type="l",xlab="",ylab="",axes=FALSE,col=2)
		par(new=T)
		plot(data[3,],type="l",xlab="",ylab="",axes=FALSE,col=3)
		par(new=T)
		plot(data[4,],type="l",xlab="",ylab="",axes=FALSE,col=4)
		par(new=T)
		plot(data[5,],type="l",xlab="",ylab="",axes=FALSE,col=6)
		par(new=T)
		plot(data[6,],type="l",xlab="",ylab="",axes=FALSE,col=8)
		par(new=T)
		plot(data[7,],type="l",xlab="",ylab="",axes=FALSE,col=9)
		par(new=T)
		plot(data[8,],type="l",xlab="",ylab="",axes=FALSE,col=10)
		par(new=T)
		plot(data[9,],type="l",xlab="",ylab="",axes=FALSE,col=11)
		par(new=T)
		plot(data[10,],type="l",xlab="",ylab="",axes=FALSE,col=12)
		par(new=T)
		plot(data[11,],type="l",xlab="",ylab="",axes=FALSE,col=13)
		par(new=T)
		plot(data[12,],type="l",xlab="",ylab="",axes=FALSE,col=14)
		par(new=T)
		plot(data[13,],type="l",xlab="",ylab="",axes=FALSE,col=15)
		par(new=T)
		plot(data[14,],type="l",xlab="",ylab="",axes=FALSE,col=16)
		par(new=T)
		plot(data[15,],type="l",xlab="",ylab="",axes=FALSE,col=17)
		par(new=T)
		plot(data[16,],type="l",xlab="",ylab="",axes=FALSE,col=18)
		#�o�̓t�@�C����
		outputFile <- paste(substr(path, 1, nchar(path) - 4),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�e�t�@�C���o��
		cat(paste("File",outputFile," OK!\n"))
	
		#���W�o��
		#cat("-----------Output Data-------------\n")
		#str(outputData)
}

#TEST�p�FBMP�t�@�C���Ǎ���
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()